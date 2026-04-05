# Expert Knowledge Gap Detection System

A machine learning-powered web application that detects and prioritizes knowledge gaps in learner profiles using K-Means clustering, statistical analysis, and risk scoring.

## Overview

This system analyzes learner performance across five key academic topics and provides:
- **Cluster-based learner profiling** - Groups learners into distinct knowledge profiles
- **Gap analysis** - Identifies specific knowledge deficiencies relative to cluster benchmarks
- **Risk scoring** - Quantifies the urgency of skill gaps using weighted metrics
- **Intervention planning** - Generates personalized learning recommendations with actionable timelines

## Features

### 📊 Core Capabilities
- **Multi-dimensional Scoring**: Evaluates learners across 5 topics:
  - Algebra
  - Geometry
  - Statistics
  - Programming
  - Problem Solving

- **Confidence Metrics**: Measures prediction reliability based on centroid distance and cluster analysis

- **Weighted Risk Assessment**: Calculates priority using:
  - Gap magnitude (learner score vs. target)
  - Topic importance weights
  - Cluster baseline statistics

- **Dynamic Severity Classification**: Categorizes gaps as High, Medium, Low, or On Track

- **Adaptive Intervention Planning**: Recommends:
  - Learning intensity (2-5 sessions/week based on risk tier)
  - Specific topic focus areas
  - Customizable action horizons (2, 4, or 8 weeks)

### 📈 Analysis Outputs
- **Gap Analysis Table**: Ranked by priority with detailed deficit metrics
- **Topic Comparison**: Visual comparison across learner, cluster, and target scores
- **Intervention Planner**: Week-by-week learning roadmaps
- **Data Export**: Download results for external tools

## Project Structure

```
ml 2006/
├── app.py                           # Main Streamlit web application
├── knowledge_gap_detection_web.ipynb # Jupyter notebook with analysis & model training
├── kmeans_model.pkl                 # Trained K-Means clustering model
├── scaler.pkl                       # Feature preprocessing scaler
├── features.pkl                     # Feature names and configuration
├── cluster_profiles.pkl             # Average scores per cluster
├── cluster_gaps.pkl                 # Gap statistics per cluster
├── cluster_distance_stats.pkl       # Centroid distance metrics
├── topic_targets.pkl                # Target score benchmarks
├── priority_weight_map.pkl          # Topic importance weights
├── remediation_map.pkl              # Intervention strategies
└── New folder/                      # Additional resources
```

## Installation & Setup

### Prerequisites
- Python 3.8+
- Streamlit
- NumPy
- Pandas
- Scikit-learn
- Joblib

### Setup Steps

1. **Create Virtual Environment** (if not already done):
   ```powershell
   python -m venv .venv
   ```

2. **Activate Environment**:
   ```powershell
   .\.venv\Scripts\Activate.ps1
   ```

3. **Install Dependencies**:
   ```bash
   pip install streamlit numpy pandas scikit-learn joblib
   ```

## Usage

### Running the Web Application

```bash
streamlit run app.py
```

The application will launch at `http://localhost:8501`

### Using the Interface

1. **Sidebar Controls**:
   - Adjust gap threshold (40-80 points)
   - Set confidence alert floor (20%-80%)
   - Select action horizon (2, 4, or 8 weeks)

2. **Input Scores**:
   - Use sliders to enter learner scores (0-100) for each topic
   - Click "Analyze Learner Profile" to run prediction

3. **Review Results**:
   - **Summary Tab**: Cluster assignment, confidence, risk score
   - **Gap Analysis Tab**: Ranked list of knowledge deficiencies
   - **Topic Comparison Tab**: Visual performance benchmarking
   - **Intervention Planner**: Actionable weekly learning plan
   - **Export Tab**: Download results as CSV/JSON

### Using the Jupyter Notebook

The `knowledge_gap_detection_web.ipynb` notebook contains:
- Model training and validation
- EDA (exploratory data analysis)
- Cluster profiling and visualization
- Statistical analysis of cluster characteristics

To run:
```bash
jupyter notebook knowledge_gap_detection_web.ipynb
```

## Technical Details

### Machine Learning Pipeline

1. **Feature Scaling**: StandardScaler normalizes input scores
2. **Clustering**: K-Means groups learners into knowledge profiles
3. **Distance Metrics**: Euclidean distance from cluster centroids
4. **Confidence Scoring**: Based on relative distance to all clusters
5. **Risk Calculation**: Weighted sum of normalized gaps

### Risk Tier Thresholds

| Risk Tier | Score Range | Recommended Intensity |
|-----------|-------------|-----------------------|
| Critical  | ≥ 35        | 5 focused sessions/week |
| High      | 22-34       | 4 focused sessions/week |
| Moderate  | 10-21       | 3 focused sessions/week |
| Low       | < 10        | 2 maintenance sessions/week |

### Severity Classification

- **High**: Gap ≥ 20 points
- **Medium**: Gap 10-19 points
- **Low**: Gap 1-9 points
- **On Track**: Gap ≤ 0 points

## Configuration

Adjustable parameters in app.py:
- `gap_threshold`: Minimum score to flagging weak topics (default: 60)
- `confidence_floor`: Alert threshold for unreliable predictions (default: 0.45)
- `action_horizon`: Intervention planning timeline
- Risk tier thresholds and weekly intensity recommendations

## Output Data

### Gap Analysis Table Columns
- **Topic**: Subject area
- **Learner_Score**: Current performance
- **Cluster_Average**: Peer benchmark
- **Suggested_Target**: Recommended score
- **Priority_Weight**: Topic importance factor
- **Gap_to_Target**: Points needed to reach target
- **Weighted_Deficit**: Priority-adjusted gap
- **Severity**: High/Medium/Low/On Track
- **Priority_Rank**: Intervention priority order

## Development Notes

- Models and scalers are pre-trained (`*.pkl` files)
- Feature names stored in `features.pkl`
- Cluster metadata cached for performance
- Web interface uses Streamlit for rapid prototyping

## Future Enhancements

- [ ] Real-time model retraining
- [ ] User profile persistence
- [ ] Integration with learning management systems
- [ ] Advanced visualization dashboards
- [ ] Multi-language support
- [ ] Mobile-responsive design

## License

[Specify your license]

## Author

[Your Name/Organization]

---

**Last Updated**: April 2026
