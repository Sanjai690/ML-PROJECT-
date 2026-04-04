
import streamlit as st
import numpy as np
import pandas as pd
import joblib

st.set_page_config(page_title='Knowledge Gap Detector - Expert', page_icon='📘', layout='wide')

kmeans = joblib.load('kmeans_model.pkl')
scaler = joblib.load('scaler.pkl')
features = joblib.load('features.pkl')
cluster_profiles = joblib.load('cluster_profiles.pkl')
cluster_gaps = joblib.load('cluster_gaps.pkl')
cluster_distance_stats = joblib.load('cluster_distance_stats.pkl')
topic_targets = joblib.load('topic_targets.pkl')
priority_weight_map = joblib.load('priority_weight_map.pkl')
remediation_map = joblib.load('remediation_map.pkl')

st.title('Expert Knowledge Gap Detection')
st.caption('Cluster prediction + confidence + weighted risk score + intervention planner')

with st.sidebar:
    st.header('Controls')
    gap_threshold = st.slider('Gap threshold', 40, 80, 60)
    confidence_floor = st.slider('Low confidence alert threshold', 0.20, 0.80, 0.45)
    action_horizon = st.selectbox('Action horizon', ['2 Weeks', '4 Weeks', '8 Weeks'], index=1)

left, right = st.columns([1, 1])

with left:
    st.subheader('Input Scores')
    algebra = st.slider('Algebra', 0, 100, 65)
    geometry = st.slider('Geometry', 0, 100, 60)
    statistics = st.slider('Statistics', 0, 100, 55)
    programming = st.slider('Programming', 0, 100, 70)
    problem_solving = st.slider('Problem Solving', 0, 100, 58)

    user_values = [algebra, geometry, statistics, programming, problem_solving]
    user_input = np.array([user_values])

if st.button('Analyze Learner Profile', use_container_width=True):
    user_scaled = scaler.transform(user_input)
    distances = kmeans.transform(user_scaled)[0]
    pred_cluster = int(np.argmin(distances))
    assigned_distance = float(distances[pred_cluster])
    confidence = float(1 - (assigned_distance / (distances.sum() + 1e-9)))
    confidence = float(np.clip(confidence, 0.0, 1.0))

    profile_row = cluster_profiles.loc[pred_cluster]
    target_row = topic_targets.loc[pred_cluster]

    gap_table = pd.DataFrame({
        'Topic': features,
        'Learner_Score': user_values,
        'Cluster_Average': [round(profile_row[f], 1) for f in features],
        'Suggested_Target': [round(target_row[f], 1) for f in features],
        'Priority_Weight': [float(priority_weight_map.get(f, 0.2)) for f in features]
    })

    gap_table['Gap_to_Target'] = (gap_table['Suggested_Target'] - gap_table['Learner_Score']).round(1)
    gap_table['Gap_to_Cluster_Avg'] = (gap_table['Cluster_Average'] - gap_table['Learner_Score']).round(1)
    gap_table['Weighted_Deficit'] = ((gap_table['Gap_to_Target'].clip(lower=0) / 100) * gap_table['Priority_Weight']).round(4)

    weak_topics = gap_table.loc[gap_table['Learner_Score'] < gap_threshold, 'Topic'].tolist()

    def severity(v):
        if v >= 20:
            return 'High'
        if v >= 10:
            return 'Medium'
        if v > 0:
            return 'Low'
        return 'On Track'

    gap_table['Severity'] = gap_table['Gap_to_Target'].apply(severity)
    gap_table['Priority_Rank'] = gap_table['Weighted_Deficit'].rank(ascending=False, method='dense').astype(int)

    risk_score = float(np.clip(gap_table['Weighted_Deficit'].sum() * 100, 0, 100))
    if risk_score >= 35:
        risk_tier = 'Critical'
        weekly_intensity = '5 focused sessions / week'
    elif risk_score >= 22:
        risk_tier = 'High'
        weekly_intensity = '4 focused sessions / week'
    elif risk_score >= 10:
        risk_tier = 'Moderate'
        weekly_intensity = '3 focused sessions / week'
    else:
        risk_tier = 'Low'
        weekly_intensity = '2 maintenance sessions / week'

    with right:
        st.subheader('Prediction Summary')
        st.success(f'Predicted Learner Cluster: {pred_cluster}')
        st.write(f'Centroid distance: **{assigned_distance:.3f}**')

        st.write('Confidence')
        st.progress(confidence)
        st.write(f'Confidence score: **{confidence:.2%}**')
        if confidence < confidence_floor:
            st.warning('Prediction confidence is low. Collect more evidence before intervention decisions.')

        baseline_mean = float(cluster_distance_stats.loc[pred_cluster, 'mean'])
        baseline_std = float(cluster_distance_stats.loc[pred_cluster, 'std'])
        st.caption(f'Cluster baseline centroid distance: mean={baseline_mean:.3f}, std={baseline_std:.3f}')

        st.metric('Weighted Risk Score', f'{risk_score:.1f}/100')
        st.metric('Risk Tier', risk_tier)

    tabs = st.tabs(['Gap Analysis', 'Topic Comparison', 'Intervention Planner', 'Export'])

    with tabs[0]:
        st.subheader('Gap Analysis Table')
        st.dataframe(gap_table.sort_values('Priority_Rank'), use_container_width=True)

    with tabs[1]:
        compare_df = pd.DataFrame({
            'Topic': features,
            'Learner': user_values,
            'Cluster Average': [profile_row[f] for f in features],
            'Suggested Target': [target_row[f] for f in features]
        }).set_index('Topic')
        st.subheader('Topic Score Comparison')
        st.line_chart(compare_df)

    with tabs[2]:
        st.subheader('Personalized Intervention Planner')
        st.write(f'**Recommended cadence:** {weekly_intensity} for **{action_horizon}**')

        prioritized = gap_table.sort_values('Weighted_Deficit', ascending=False)
        for _, row in prioritized.iterrows():
            if row['Gap_to_Target'] <= 0:
                continue
            topic = row['Topic']
            st.markdown(f"**{topic}** | Severity: {row['Severity']} | Priority Rank: {int(row['Priority_Rank'])}")
            suggestions = remediation_map.get(topic, ['Focused revision and deliberate practice'])
            for item in suggestions:
                st.write(f'- {item}')

        if prioritized['Gap_to_Target'].max() <= 0:
            st.info('No major gaps found. Continue balanced practice and periodic assessments.')

    with tabs[3]:
        export_df = gap_table.sort_values('Priority_Rank').copy()
        csv_bytes = export_df.to_csv(index=False).encode('utf-8')
        st.download_button(
            label='Download Gap Report (CSV)',
            data=csv_bytes,
            file_name='knowledge_gap_report.csv',
            mime='text/csv'
        )
