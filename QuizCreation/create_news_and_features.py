import firebase_admin
from firebase_admin import credentials, db, firestore
import json
from datetime import datetime
import time

FIREBASE_DATABASE_URL = "https://streamliner-1-default-rtdb.firebaseio.com/"
CRED_FILE = "D:/Cred.json"
DATA_FILE = "./news_and_features/content.json"

def init_firebase():
    cred = credentials.Certificate(CRED_FILE)
    firebase_admin.initialize_app(cred, {
        'databaseURL': FIREBASE_DATABASE_URL
    })
    return db.reference(), firestore.client()

def upload_news(rtdb_ref, news_data):
    news_ref = rtdb_ref.child('news')
    for item in news_data:
        if isinstance(item.get('timestamp'), str):
            item['timestamp'] = int(time.mktime(datetime.strptime(
                item['timestamp'], "%Y-%m-%d").timetuple())) * 1000
        news_ref.push().set(item)

def upload_features(firestore_db, features_data):
    features_ref = firestore_db.collection('features')
    for item in features_data:
        if isinstance(item.get('expectedRelease'), str):
            item['expectedRelease'] = datetime.strptime(
                item['expectedRelease'], "%Y-%m-%d")
        features_ref.add(item)

def main():
    try:
        # Initialize Firebase
        rtdb_ref, firestore_db = init_firebase()
        
        # Load data
        with open(DATA_FILE, 'r') as f:
            data = json.load(f)
        
        if 'news' in data:
            upload_news(rtdb_ref, data['news'])
            print("News uploaded successfully")
            
        if 'features' in data:
            upload_features(firestore_db, data['features'])
            print("Features uploaded successfully")
            
    except Exception as e:
        print(f"Error: {str(e)}")

if __name__ == "__main__":
    main()