# Guardian Angel: Enhancing Road Safety Through Advanced Monitoring

## Overview

Guardian Angel is a comprehensive, technology-driven project designed to enhance road safety by monitoring and analyzing various factors contributing to accidents. This project integrates several components, including an Android app, a Flask web server, and advanced models for detecting drowsiness, distracted driving, and assessing driver stress through physiological data segmentation. Additionally, it encompasses an advisory control system developed in MATLAB Simulink to ensure safe driving decisions.

## Features

- **Android App**: Provides real-time video monitoring, user-friendly interface, and efficient server communication.
- **Flask Web Server**: Hosts APIs for emotion recognition, distraction prediction, and workload prediction, and interfaces with MATLAB for advisory control.
- **Real-Time Drowsiness Detection**: Utilizes video analysis and facial landmark detection to identify signs of driver fatigue.
- **Distracted Driver Detection**: Employs a convolutional neural network (CNN) to identify various distracted driving behaviors.
- **Stress and Workload Analysis**: Analyzes physiological data to assess cognitive workload states of drivers.
- **Advisory Control in MATLAB Simulink**: Determines the safety of switching to automated driving mode based on driver mood and attentiveness.

## Installation and Setup

1. **Android App**:
    - Download the APK file and install it on your Android device.
    - Ensure camera permissions are granted for real-time monitoring.

2. **Flask Web Server**:
    - Requires Python 3.x.
    - Install dependencies: `pip install -r requirements.txt`
    - Run the server: `python app.py`

3. **Model Implementations**:
    - Ensure Python 3.x is installed with libraries such as OpenCV, TensorFlow, and Keras.
    - Follow the individual setup instructions provided in the respective model directories.

4. **MATLAB Simulink**:
    - Requires MATLAB and Simulink.
    - Open the provided `.slx` file in MATLAB to view and run the advisory control model.

## Usage

- **Android App**: Launch the app and follow the on-screen instructions to start the monitoring process.

## Background

- **Flask Server**: The server runs on `localhost` by default. Use the provided API endpoints for various functionalities.
- **Models**: Execute the Python scripts for each model to start the detection processes.
- **MATLAB Simulink**: Run the model to simulate the advisory control logic.
