import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json
import argparse

def init_firebase():

    cred = credentials.Certificate('path/to/your/serviceAccountKey.json')
    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://your-project-id.firebaseio.com'
    })

def create_quiz(quiz_data):

    ref = db.reference('quizzes')


    new_quiz_ref = ref.push()
    new_quiz_ref.set(quiz_data)

    print(f"Quiz created successfully with ID: {new_quiz_ref.key}")

def main():
    parser = argparse.ArgumentParser(description='Create a quiz in Firebase')
    parser.add_argument('quiz_file', type=str, help='Path to quiz JSON file')
    args = parser.parse_args()


    init_firebase()


    with open(args.quiz_file, 'r') as f:
        quiz_data = json.load(f)


    create_quiz(quiz_data)

if __name__ == '__main__':
    main()


