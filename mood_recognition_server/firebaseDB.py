import firebase_admin
from firebase_admin import credentials, db

cred = credentials.Certificate("cse535project4ui-firebase-adminsdk-wz0i9-abd3ab36e6.json")
firebase_admin.initialize_app(cred, {"databaseURL":"https://cse535project4ui-default-rtdb.firebaseio.com/"})


def create_or_update_record(record_id, data):
    try:
        # Reference to the database
        ref = db.reference(f"{record_id}")
        # Update specific fields or create a new record
        ref.update(data)
        print(f"Record {record_id} created/updated successfully")
        return True
    except Exception as e:
        print(f"Error: {e}")
        return False
