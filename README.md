**Guardian Angel: Enhancing Road Safety through Comprehensive Driver Monitoring**

## Project Overview:
Guardian Angel is an initiative aimed at improving road safety by integrating cutting-edge technology into the driving experience. The project envisions a comprehensive system that monitors various facets of driving behavior to ensure a secure and reliable journey for every road user. My contribution to this project focuses on the implementation of the Distracted Driver Detection System, a pivotal component designed to identify and address distracted driving behaviors.

## Distracted Driver Detection System:
### Objective:
The primary goal of the Distracted Driver Detection System is to enhance safety on the roads by identifying instances of distracted driving. Leveraging advanced machine learning techniques, the system analyzes real-time images captured from the driver's perspective and alerts users when distracted behaviors are detected.

### Distracted Driving Behaviours:

- normal driving
- texting - right
- talking on the phone - right
- texting - left
- talking on the phone - left
- operating the radio
- drinking
- reaching behind
- hair and makeup
- talking to passenger

### Key Features:
- **Multi-Model Approach:** Utilizing a convolutional neural network (CNN), the system distinguishes between various distracted driving behaviors, including texting, talking on the phone, and more.
  
- **Integration with Guardian Angel Framework:** The distracted driver detection component seamlessly integrates into the broader Guardian Angel architecture, contributing to the overall safety decision-making process.

- **Adaptive Strategies:** The system incorporates adaptive strategies such as transfer learning with pre-trained models and Leslie Smith's One Cycle Policy, optimizing performance and efficiency.

## How to Use:

use inference.py to detect if the driver is detected

## How to run

I have uploaded the notebooks to kaggle, to run the training and inference notebooks just go to below links, click copy and edit and then click run all you'll see the inference results.

- [Kaggle Inference Link](https://www.kaggle.com/code/vineethkanaparthi/ddd-inference/notebook)
- [Kaggle Training Link](https://www.kaggle.com/code/vineethkanaparthi/ddd-fastaiv3)

## Repository Structure:

- **/distracted-driver-detection:** Encompasses the code and resources related to the distracted driver detection model, implemented using fastai and Keras.

## Dependencies:
- Python 3.x
- Fastai
- Keras

## Acknowledgments:
This project is a collaborative effort, and I extend my gratitude to all team members contributing to the Guardian Angel initiative. Together, we strive to improve road safety through innovation and technology.


**Happy Driving and Stay Safe!**