# Project 4 - Analyzing Driver Stress and Workload Through Physiological Data Segmentation

## Overview
This component(Project 4) is part of Project 5, focusing on enhancing road safety by analyzing drivers' cognitive workload using physiological data. It categorizes drivers into Low Cognitive Workload (LCW) and High Cognitive Workload (HCW) states based on heart rate, electrodermal activity, blood volume pulse, and temperature.

## Features
- Uses physiological data to assess cognitive workload.
- Utilizes K-means clustering for categorizing LCW and HCW states.
- Analyzes time-series data from various physiological metrics.

## How it Works
1. **Data Preparation:** Extracts and prepares time-series physiological data (HR, EDA, BVP, TEMP) from [AffectiveROAD](https://www.media.mit.edu/tools/affectiveroad/) dataset.
2. **Feature Preparation:** Prepares the features by calculating statistical measures.
3. **Categorization:** Applies K-means clustering on the time series data of multiple features to categorize into LCW and HCW.

## Requirements
- Python 3.x
- Pandas
- NumPy
- Scikit-learn

## Installation and Usage
1. Install Python 3.x on your system.
2. Install necessary libraries using pip:

`pip install pandas numpy scikit-learn`

3. Download the project to your local machine.
4. Navigate to the directory and execute:

`python3 code.py`



