package com.example.streamliner.studyMaterials;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.streamliner.R;

import java.util.List;

public class QuestionFragment extends Fragment {
    private TextView questionTitleTV;
    private RadioGroup answersRadioGroup;
    private RadioButton answer1RB, answer2RB, answer3RB;
    private Button previousButton, nextButton;
    private Question currentQuestion;
    private int currentPosition;
    private int totalQuestions;
    private String type; // type of activity calling the fragment. Either practice or quiz
    private OnQuestionInteractionListener listener;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public interface OnQuestionInteractionListener {
        void onPreviousClicked();
        void onNextClicked();
        void onAnswerSelected(int answerIndex);
        void onSubmitClicked();
    }

    public static QuestionFragment newInstance(Question question, int position, int total, String type) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable("question", question);
        args.putInt("position", position);
        args.putInt("total", total);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        questionTitleTV = view.findViewById(R.id.questionTitleTV);
        answersRadioGroup = view.findViewById(R.id.answersRadioGroup);
        answer1RB = view.findViewById(R.id.answer1RB);
        answer2RB = view.findViewById(R.id.answer2RB);
        answer3RB = view.findViewById(R.id.answer3RB);
        previousButton = view.findViewById(R.id.previousButton);
        nextButton = view.findViewById(R.id.nextButton);

        if (getArguments() != null) {
            currentQuestion = getArguments().getParcelable("question");
            //Log.e("currentQuestion", currentQuestion.getTitle());
            currentPosition = getArguments().getInt("position");
            totalQuestions = getArguments().getInt("total");
            type = getArguments().getString("type");
            setupQuestion();
        }

        return view;
    }

    private void setupQuestion() {
        String title = currentPosition + 1 + ". " + currentQuestion.getTitle();
        questionTitleTV.setText(title);
        if (currentQuestion.getTitle() == null) {
            Log.e("questionTitle", "null");
        }

        List<String> answers = currentQuestion.getAnswers();

        answer1RB.setText(answers.get(0));
        answer2RB.setText(answers.get(1));
        answer3RB.setText(answers.get(2));

        // Enable Previous button after the first question
        previousButton.setEnabled(currentPosition > 0);

        answersRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (listener != null) {
                if (type.equals("quiz")) { // QuizFragment
                    if (checkedId == answer1RB.getId()) {
                        listener.onAnswerSelected(0);
                    } else if (checkedId == answer2RB.getId()) {
                        listener.onAnswerSelected(1);
                    } else if (checkedId == answer3RB.getId()) {
                        listener.onAnswerSelected(2);
                    }
                }
                else { // PracticeFragment, type = practice -> show feedback when user clicks an answer
                    int selectedAnswer = -1;
                    if (checkedId == answer1RB.getId()) selectedAnswer = 0;
                    else if (checkedId == answer2RB.getId()) selectedAnswer = 1;
                    else if (checkedId == answer3RB.getId()) selectedAnswer = 2;

                    if (selectedAnswer != -1) {
                        listener.onAnswerSelected(selectedAnswer);
                    }
                    /*
                    if (selectedAnswer != -1) {
                        listener.onAnswerSelected(selectedAnswer);
                        boolean isCorrect = selectedAnswer == currentQuestion.getCorrectIndex();
                        PracticeFragment practiceFragment = (PracticeFragment) getChildFragmentManager()
                                .findFragmentByTag("PracticeFragment");

                        if (practiceFragment != null) {
                            practiceFragment.showAnswerFeedback(isCorrect);
                        }
                    }*/
                }
            }
        });

        // Handle state once user has answered the last question
        if (currentPosition == totalQuestions - 1) {
            if (type.equals("practice")) {
                nextButton.setText("Submit");
            }
            else {
                nextButton.setEnabled(false);
                nextButton.setAlpha(0.5f);
                listener.onSubmitClicked();
            }
        }


        previousButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPreviousClicked();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (listener != null) {
                if (currentPosition == totalQuestions - 1) {
                    listener.onSubmitClicked();
                } else {
                    listener.onNextClicked();
                }
            }
        });
    }

    public void setListener(OnQuestionInteractionListener listener) {
        this.listener = listener;
    }
}