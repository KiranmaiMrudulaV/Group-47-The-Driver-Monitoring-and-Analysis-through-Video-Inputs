# Emotion Recognition Web Service

This Flask web server provides a simple API for emotion recognition, distraction prediction, and workload prediction using machine learning models.

## Getting Started

1. **Install dependencies:**

    ```bash
    pip install flask
    ```

2. **Clone the repository:**

    ```bash
    git clone https://github.com/rishikumar-p/emotionrecognition.git
    cd emotionrecognition
    ```

3. **Run the server:**

    ```bash
    python app.py
    ```

    The server will run on `http://127.0.0.1:5000/` by default.

## Endpoints

### 1. Info

- **Endpoint:** `/`
- **Method:** GET
- **Description:** Returns a simple info message regarding the service.

### 2. Emotion Recognition

- **Endpoint:** `/emotion`
- **Method:** POST
- **Description:** Upload an image/video file and receive emotion prediction.
- **Request:**
  - Form Data: `file` (image/video file)
- **Response:**
  - JSON: `{ "prediction": "emotion_result" }`

### 3. Distraction Prediction

- **Endpoint:** `/distraction`
- **Method:** POST
- **Description:** Upload an image/video file and receive distraction prediction.
- **Request:**
  - Form Data: `file` (image/video file)
- **Response:**
  - JSON: `{ "prediction": "distraction_result" }`

### 4. Workload Prediction

- **Endpoint:** `/workload`
- **Method:** POST
- **Description:** Upload an image/video file and receive workload prediction.
- **Request:**
  - Form Data: `file` (image/video file)
- **Response:**
  - JSON: `{ "prediction": "workload_result" }`


## Testing a Deployed Service

- I have already deployed the RESTful flask web service in AWS EC2 using gUnicorn wsgi, and Nginx reverse proxy.
- If you want to test it, here is the public url. You can easily test it using Postman. 
```
Here are the endpoints

http://54.183.16.150/
http://54.183.16.150/distraction
http://54.183.16.150/emotion
http://54.183.16.150/workload

``` 
- You can easily test the above endpoints using postman
- Please use POST method for the /distraction, /emotion, /workload.
- Send the image/video file in body with key `image` and file attached to it.

## Errors

- If there is an issue with the request or processing, an error message in JSON format will be returned.

## Example Usage

- Using [curl](https://curl.se/):

    ```bash
    curl http://127.0.0.1:5000/
    curl -X POST -F "file=@path/to/your/file" http://127.0.0.1:5000/emotion
    ```
