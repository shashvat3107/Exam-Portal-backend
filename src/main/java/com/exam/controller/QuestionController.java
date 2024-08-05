package com.exam.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.model.exam.Question;
import com.exam.model.exam.Quiz;
import com.exam.service.QuestionService;
import com.exam.service.QuizService;

@RestController
@CrossOrigin("*")
@RequestMapping("/question")
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    //add question
    @PostMapping("/")
    public ResponseEntity<Question> add(@RequestBody Question question){
        return ResponseEntity.ok(this.questionService.addQuestion(question));
    }

    //update question
    @PutMapping("/")
    public ResponseEntity<Question> update(@RequestBody Question question){
        return ResponseEntity.ok(this.questionService.updateQuestion(question));
    }

    //get all question of any id
    @GetMapping("/quiz/{qid}")
    public ResponseEntity<?> getQuestionsOfQuiz(@PathVariable("qid") Long qid){
        // Quiz quiz = new Quiz();
        // quiz.setQid(qid);
        // Set<Question> questionsOfQuiz = this.questionService.getQuestionsOfQuiz(quiz);
        // return ResponseEntity.ok(questionsOfQuiz);

        Quiz quiz = this.quizService.getQuiz(qid);
        Set<Question> questions = quiz.getQuestions();
        List list = new ArrayList(questions);
        if(list.size()>Integer.parseInt(quiz.getNoOfQuestions())){
            list = list .subList(0, Integer.parseInt(quiz.getNoOfQuestions()+1));
        }
        list.forEach((q)->{
            ((Question) q).setAnswer("");
        });

        Collections.shuffle(list);
        return ResponseEntity.ok(list);
    }


    @GetMapping("/quiz/all/{qid}")
    public ResponseEntity<?> getQuestionsOfQuizAdmin(@PathVariable("qid") Long qid){
        Quiz quiz = new Quiz();
        quiz.setQid(qid);
        Set<Question> questionsOfQuiz = this.questionService.getQuestionsOfQuiz(quiz);
        return ResponseEntity.ok(questionsOfQuiz);

        // Quiz quiz = this.quizService.getQuiz(qid);
        // Set<Question> questions = quiz.getQuestions();
        // List list = new ArrayList(questions);
        // if(list.size()>Integer.parseInt(quiz.getNoOfQuestions())){
        //     list = list .subList(0, Integer.parseInt(quiz.getNoOfQuestions()+1));
        // }
        // Collections.shuffle(list);
        // return ResponseEntity.ok(list);
    }

    //get single question..
    @GetMapping("/{quesId}")
    public Question get(@PathVariable("quesId") Long quesId){
        return this.questionService.getQuestion(quesId);
    }

    //delete question
    @DeleteMapping("/{quesId}")
    public void delete(@PathVariable("quesId") Long quesId){
        this.questionService.deleteQuestion(quesId);
    }


    //evaluate quiz
    @PostMapping("/eval-quiz")
    public ResponseEntity<?>evalQuiz(@RequestBody List<Question> questions){
        System.out.println(questions);

        double marksGot = 0;
        int correctAnswers = 0;
        int attempted = 0;

        for(Question q : questions){
            // System.out.println(q.getGivenAnswer());
            Question question = this.questionService.get(q.getQuesId());
            if(question.getAnswer().equals(q.getGivenAnswer()))
            {
                //correct
                correctAnswers++;
                double marksSingle = Double.parseDouble(questions.get(0).getQuiz().getMaxMarks())/questions.size();
                marksGot += marksSingle;
            }
            if(q.getGivenAnswer()!=null){
                attempted++;
            }
        };

        Map<String,Object> map = Map.of("marksGot",marksGot,"correctAnswers",correctAnswers,"attempted",attempted);
        return ResponseEntity.ok(map);
    }











}
