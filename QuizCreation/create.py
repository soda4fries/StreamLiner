import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json
import os


FIREBASE_DATABASE_URL = "https://streamliner-1-default-rtdb.firebaseio.com/"  
CRED_FILE = "Cred.json"  
QUIZ_DIRECTORY = "quiz"  

def init_firebase():
    try:
       
        cred = credentials.Certificate(CRED_FILE)

        
        firebase_admin.initialize_app(cred, {
            'databaseURL': FIREBASE_DATABASE_URL
        })
        print("Firebase initialized successfully")
    except Exception as e:
        print(f"Failed to initialize Firebase: {str(e)}")
        raise

def create_quiz(quiz_data, file_name):
    try:
        
        ref = db.reference('quizzes')

        
        new_quiz_ref = ref.push()
        new_quiz_ref.set(quiz_data)

        print(f"Quiz from {file_name} created successfully with ID: {new_quiz_ref.key}")
        return True
    except Exception as e:
        print(f"Failed to create quiz from {file_name}: {str(e)}")
        return False

def main():
    # Initialize Firebase
    init_firebase()

    
    if not os.path.exists(QUIZ_DIRECTORY):
        print(f"Creating quiz directory: {QUIZ_DIRECTORY}")
        os.makedirs(QUIZ_DIRECTORY)
        print("Please place your quiz JSON files in the quiz directory and run the script again.")
        return

  
    quiz_files = [f for f in os.listdir(QUIZ_DIRECTORY) if f.endswith('.json')]

    if not quiz_files:
        print("No JSON files found in quiz directory!")
        return

    print(f"Found {len(quiz_files)} quiz files")


    successful_uploads = 0
    for file_name in quiz_files:
        file_path = os.path.join(QUIZ_DIRECTORY, file_name)
        print(f"\nProcessing {file_name}...")

        try:
           
            with open(file_path, 'r') as f:
                quiz_data = json.load(f)

      
            required_fields = ['title', 'description', 'topic', 'questions']
            missing_fields = [field for field in required_fields if field not in quiz_data]

            if missing_fields:
                print(f"Quiz file {file_name} is missing required fields: {', '.join(missing_fields)}")
                continue

           
            if create_quiz(quiz_data, file_name):
                successful_uploads += 1

        except json.JSONDecodeError:
            print(f"Failed to parse {file_name}: Invalid JSON format")
        except Exception as e:
            print(f"Error processing {file_name}: {str(e)}")

    print(f"\nUpload complete! Successfully uploaded {successful_uploads} out of {len(quiz_files)} quizzes")

if __name__ == '__main__':
    main()