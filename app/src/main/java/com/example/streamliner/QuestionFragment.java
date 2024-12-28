package com.example.streamliner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {
    private TextView questionTitleTV;
    private RadioGroup answersRadioGroup;
    private RadioButton answer1RB, answer2RB, answer3RB;
    private Button previousButton, nextButton;
    private Question currentQuestion;
    private int currentPosition;
    private int totalQuestions;
    private OnQuestionInteractionListener listener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    public QuestionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public interface OnQuestionInteractionListener {
        void onPreviousClicked();
        void onNextClicked();
        void onAnswerSelected(int answerIndex);
        void onSubmitClicked();
    }

    public static QuestionFragment newInstance(Question question, int position, int total) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable("question", question);
        args.putInt("position", position);
        args.putInt("total", total);
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
            setupQuestion();
        }

        return view;
    }

    private void setupQuestion() {

        //Log.e("questionTitle", currentQuestion.getTitle());
        List<String> answers = currentQuestion.getAnswers();
        Log.e("questionTitle", currentQuestion.getAnswers().get(0));

        answer1RB.setText(answers.get(0));
        answer2RB.setText(answers.get(1));
        answer3RB.setText(answers.get(2));

        questionTitleTV.setText(currentQuestion.getTitle());
        if (currentQuestion.getTitle() == null) {
            Log.e("questionTitle", "null");
        }

        // Handle navigation button states
        previousButton.setEnabled(currentPosition > 0);
        if (currentPosition == totalQuestions - 1) {
            nextButton.setText("Submit");
        }

        answersRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (listener != null) {
                if (checkedId == answer1RB.getId()) {
                    listener.onAnswerSelected(0);
                } else if (checkedId == answer2RB.getId()) {
                    listener.onAnswerSelected(1);
                } else if (checkedId == answer3RB.getId()) {
                    listener.onAnswerSelected(2);
                }
            }
        });

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