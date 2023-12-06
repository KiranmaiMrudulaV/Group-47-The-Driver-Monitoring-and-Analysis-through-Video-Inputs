import os
from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
from predictEmotion import getEmotion
from predictDistraction import getDistraction
from predictWorkload import getWorkload
from firebaseDB import create_or_update_record
import json
import random

app = Flask(__name__)

@app.route('/')
def hello_world():
    with open('info.json', 'r') as file:
        endpoints_data = json.load(file)
    return jsonify(endpoints_data)

@app.route('/emotion', methods=['POST'])
def emotion():
    print(request.files)
    try:
        if 'image' not in request.files:
            return jsonify({'error': 'file not provided'}), 400
        if 'id' not in request.form:
            return jsonify({'error': 'id not provided'}), 400
        file = request.files['image']
        id = request.form['id']
        if file.filename =='':
            return jsonify({'error': 'no file selected'}), 400 
        if file:
            filename = secure_filename(file.filename)
            result = getEmotion(file)
            probability = getRandomProbability()
            create_or_update_record(id, {"UUID": id, "emotion": result, "drowsinessProbability": probability, "hr": getRandomHR(), "rr": getRandomRR()} )
            return jsonify({'prediction': result, 'id': id, 'drowsinessProbability': probability}), 200
    except:
        return jsonify({'error': 'something went wrong'}), 500
    

@app.route('/distraction', methods=['POST'])
def distraction():
    try:
        if 'image' not in request.files:
            return jsonify({'error': 'file not provided'}), 400
        file = request.files['image']
        id = request.form['id']
        if file.filename =='':
            return jsonify({'error': 'no file selected'}), 400
        if file:
            filename = secure_filename(file.filename)
            result = getDistraction(file)
            probability = getRandomProbability()
            create_or_update_record(id, {"UUID": id, "distraction": result, "distractionProbability": probability} )
            return jsonify({'id': id, 'prediction': result,  'distractionProbability': probability}), 200
    except:
        return jsonify({'error': 'something went wrong'}), 500

@app.route('/workload', methods=['POST'])
def workload():
    try:
        if 'image' not in request.files:
            return jsonify({'error': 'file not provided'}), 400
        file = request.files['image']
        id = request.form['id']
        if file.filename =='':
            return jsonify({'error': 'no file selected'}), 400
        if file:
            filename = secure_filename(file.filename)
            result = getWorkload(file)
            probability = getRandomProbability()
            create_or_update_record(id, {"UUID": id, "workload": result, "workloadProbability": probability} )
            return jsonify({'id': id,'prediction': result, 'workloadProbability': probability}), 200
    except:
        return jsonify({'error': 'something went wrong'}), 500

def getRandomProbability():
   return random.randint(0,10)/10

def getRandomHR():
    return random.randint(60, 100)

def getRandomRR():
    return random.randint(14, 20)

if __name__ == '__main__':
    app.run(debug=True)
