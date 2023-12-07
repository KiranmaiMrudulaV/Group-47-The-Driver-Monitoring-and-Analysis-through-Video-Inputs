import pandas as pd
import numpy as np
import os
from sklearn.cluster import KMeans

def extract_features(data):
    return {
        'mean': data.mean(),
        'std': data.std(),
        'min': data.min(),
        'max': data.max()
    }

def prepare_features(subdir):
    feature_files = {
        'HR': 'HR.csv',
        'EDA': 'EDA.csv',
        'BVP': 'BVP.csv',
        'TEMP': 'TEMP.csv'
    }

    combined_features = {}

    for feature_type, file_suffix in feature_files.items():
        feature_file = [os.path.join(subdir, file) for file in os.listdir(subdir) if file.endswith(file_suffix)][0]
        feature_data = pd.read_csv(feature_file).iloc[1:]
        feature_stats = extract_features(feature_data.iloc[:, 0])
        combined_features.update({f"{feature_type}_{stat}": value for stat, value in feature_stats.items()})

    return combined_features

def train_model(main_directory_path):
    print("Training the model with existing data...")

    features = []

    subdirectories = sorted([os.path.join(main_directory_path, d) for d in os.listdir(main_directory_path) if os.path.isdir(os.path.join(main_directory_path, d))], 
                            key=lambda x: int(x.split('_')[-1]))

    for subdir in subdirectories:
        combined_features = prepare_features(subdir)
        features.append(combined_features)

    features_df = pd.DataFrame(features)

    model = KMeans(n_clusters=2, random_state=42)

    model.fit(features_df)

    cluster_labels = model.predict(features_df)

    subdir_names = [os.path.basename(subdir) for subdir in subdirectories]
    cluster_labels_dict = {subdir_names[i]: "HCW" if label == 1 else "LCW" for i, label in enumerate(cluster_labels)}

    print("\nCategorization of existing data:")
    for driver in sorted(cluster_labels_dict.keys(), key=lambda x: int(x.split('_')[-1])):
        print(f"{driver}: {cluster_labels_dict[driver]}")

    print("\nModel training complete.")
    return model

def predict_new_data_point(model, new_data_directory):
    print("\nPredicting category for new data point: (This will integarted with the mobile application to calculate for the user)")
    new_features = prepare_features(new_data_directory)
    new_features_df = pd.DataFrame([new_features])

    prediction = model.predict(new_features_df)
    category = "HCW" if prediction[0] == 1 else "LCW"
    print(f"\nPrediction for the new data ({new_data_directory}): {category}")
    print("\nPrediction complete.")

main_directory_path = 'train/'
model = train_model(main_directory_path)

new_data_directory = 'test/Driver_test'
predict_new_data_point(model, new_data_directory)
