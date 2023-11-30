import torch
from fastai.vision.all import *

# Load the exported learner
loaded_learn = load_learner('path/to/cnn_model.pth')

def make_inference(img_path):
    # Load and preprocess the single image you want to predict
    img = PILImage.create(img_path)

    # Make the prediction
    pred, _, prob = loaded_learn.predict(img)
    
    return pred, prob

# Example usage
image_path = 'path/to/test/image.jpg'
prediction, probabilities = make_inference(image_path)

print(f"Prediction: {prediction}")
print(f"Probabilities: {probabilities}")
