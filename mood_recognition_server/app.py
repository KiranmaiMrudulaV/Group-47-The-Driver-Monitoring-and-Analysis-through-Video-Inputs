import os
from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
from predictEmotion import getEmotion
from predictDistraction import getDistraction
from predictWorkload import getWorkload
import json

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
        if 'file' not in request.files:
            return jsonify({'error': 'file not provided'}), 400
        file = request.files['file']
        if file.filename =='':
            return jsonify({'error': 'no file selected'}), 400 
        if file:
            filename = secure_filename(file.filename)
            result = getEmotion(file)
            return jsonify({'prediction': result}), 200
    except:
        return jsonify({'error': 'something went wrong'}), 500
    

@app.route('/distraction', methods=['POST'])
def distraction():
    try:
        if 'file' not in request.files:
            return jsonify({'error': 'file not provided'}), 400
        file = request.files['file']
        if file.filename =='':
            return jsonify({'error': 'no file selected'}), 400
        if file:
            filename = secure_filename(file.filename)
            result = getDistraction(file)
            return jsonify({'prediction': result}), 200
    except:
        return jsonify({'error': 'something went wrong'}), 500

@app.route('/workload', methods=['POST'])
def workload():
    try:
        if 'file' not in request.files:
            return jsonify({'error': 'file not provided'}), 400
        file = request.files['file']
        if file.filename =='':
            return jsonify({'error': 'no file selected'}), 400
        if file:
            filename = secure_filename(file.filename)
            result = getWorkload(file)
            return jsonify({'prediction': result}), 200
    except:
        return jsonify({'error': 'something went wrong'}), 500

    
if __name__ == '__main__':
    app.run(debug=True)