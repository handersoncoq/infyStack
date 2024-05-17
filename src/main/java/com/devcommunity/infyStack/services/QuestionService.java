package com.devcommunity.infyStack.services;

import com.devcommunity.infyStack.dtos.requests.NewQuestionRequest;
import com.devcommunity.infyStack.dtos.requests.UpdateQuestionRequest;
import com.devcommunity.infyStack.exceptions.InvalidInputException;
import com.devcommunity.infyStack.dtos.responses.QuestionResponse;
import com.devcommunity.infyStack.exceptions.AccessDeniedException;
import com.devcommunity.infyStack.exceptions.ResourceNotFoundException;
import com.devcommunity.infyStack.models.documents.Question;
import com.devcommunity.infyStack.models.entities.Badge;
import com.devcommunity.infyStack.models.entities.Tag;
import com.devcommunity.infyStack.models.entities.User;
import com.devcommunity.infyStack.repositories.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepo;
    private final TagService tagService;
    private final UserService userService;
    private final BadgeService badgeService;


    public QuestionResponse askQuestion(String token, NewQuestionRequest newQuestionRequest){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // instantiate new question
        Question newQuestion = new Question(newQuestionRequest);
        newQuestion.setAuthor(sessionUser);
        questionRepo.save(newQuestion);

        // handle tags
        handleTags(newQuestionRequest.getTags(), newQuestion);

        // update new question and save
        newQuestion.setDateCreated(LocalDateTime.now());
        questionRepo.save(newQuestion);

        // update user's question list
        sessionUser.getQuestionIds().add(newQuestion.getId());
        userService.save(sessionUser);

        // assign badge if user's qualified
        assignBadge(sessionUser);

        return new QuestionResponse(newQuestion);
    }

    public void updateQuestion(String token, UpdateQuestionRequest updateRequest){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // make sure user is editing their own question
        if(!sessionUser.getQuestionIds().contains(updateRequest.getQuestionId()))
            throw new AccessDeniedException("You cannot modify this question");

        // find question
        Question foundQuestion = getQuestionById(updateRequest.getQuestionId());

        // update question title if exists
        if(updateRequest.getTitle() != null && !updateRequest.getTitle().isBlank()){
            foundQuestion.setTitle(updateRequest.getTitle());
        }

       // update question body if exists
        if(updateRequest.getBody() != null && !updateRequest.getBody().isBlank()){
            foundQuestion.setBody(updateRequest.getBody());
        }

        // update question code if exists
        if(updateRequest.getCode() != null && !updateRequest.getCode().isBlank()){
            foundQuestion.setCode(updateRequest.getCode());
        }

        // update question tags if any
        if(updateRequest.getTags() != null && !updateRequest.getTags().isEmpty())
            handleTags(updateRequest.getTags(), foundQuestion);

        // update and save found question
        foundQuestion.setLastEdited(LocalDateTime.now());
        questionRepo.save(foundQuestion);
    }

    public void handleTags(List<String> tagStrings, Question question){
        Predicate<String> emptyTag = String::isBlank;
        if(tagStrings.isEmpty())
            throw new InvalidInputException("No tags found");
        if(tagStrings.stream().anyMatch(emptyTag))
            throw new InvalidInputException("One or more tags were empty");

        List<Tag> tags = new ArrayList<>();
        tagStrings.forEach((tagName) ->{
            Tag tag = tagService.getTagByName(tagName);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
            }
            tag.getQuestionIds().add(question.getId());
            tagService.save(tag);
            tags.add(tag);
        });
        question.setTags(tags);
        questionRepo.save(question);

        // update tag cousins
        question.getTags().forEach(tag -> {
            tag.getCousins().addAll(
                    question.getTags().stream()
                            .map(Tag::getName)
                            .filter(name -> !name.equals(tag.getName()))
                            .collect(Collectors.toSet())
            );
            tagService.save(tag);
        });
    }

    public List<QuestionResponse> getQuestionsByTag(String tagName) {
        Tag tag = tagService.getTagByName(tagName);
        if (tag == null) {
            return Collections.emptyList(); // Returns an immutable empty list
        }

        Iterable<Question> questionIterable = questionRepo.findAllById(tag.getQuestionIds());
        List<Question> questions = new ArrayList<>();
        questionIterable.forEach(questions::add);

        return questions.stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }



    public List<QuestionResponse> getQuestionsByPage(){

        return questionRepo.findAll()
                .stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }

    public Page<QuestionResponse> getQuestionsByPage(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepo.findAll(paging);
        return questionPage.map(QuestionResponse::new);
    }

    public Question getQuestionById(String id){
        return questionRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Question does not exist"));
    }

    public void save(Question question){
        questionRepo.save(question);
    }

    public void assignBadge(User user){
        int numberOfQuestionAsked = user.getQuestionIds().size();
        Badge matchedBadge = badgeService.getByNumberOfQuestionRequired(numberOfQuestionAsked);
        if(numberOfQuestionAsked == 0 || matchedBadge == null) return;
        user.setBadge(matchedBadge);
        userService.save(user);
    }

    public List<QuestionResponse> getSimilarQuestionsBasedOnRequestTags(NewQuestionRequest questionRequest){

        List<String> requestTags = questionRequest.getTags();
        List<Question> questionList = questionRepo.findAll();
        List<Question> questionsBasedOnRequestTags = new ArrayList<>();

        for(Question question : questionList){
            int numberOfMatches = 0;
            List<String> questionTagNames = question
                    .getTags()
                    .stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());
            for(String requestTagName : requestTags){
                if(questionTagNames.contains(requestTagName.strip().replaceAll(" ", "").toLowerCase()))
                    numberOfMatches ++;
            }
            if((numberOfMatches * 100)/requestTags.size() >= 60)
                questionsBasedOnRequestTags.add(question);
        }

        if(questionsBasedOnRequestTags.isEmpty()) return new ArrayList<>();

        return questionsBasedOnRequestTags.stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }

    public void viewQuestion(@Nullable String token, String questionId){

        // if no user is logged in, find question and update without updating user's viewed questions
        if(token == null || token.isBlank() || token.length() < 7){
            Question foundQuestion = getQuestionById(questionId);
            foundQuestion.setViews(foundQuestion.getViews() + 1);
            questionRepo.save(foundQuestion);
            return;
        }

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // if the user posted this question or has already viewed the question, ignore
        if(sessionUser.getQuestionIds().contains(questionId) ||
                sessionUser.getQuestionViewedIds().contains(questionId)) return;


        // find question and update
        Question foundQuestion = getQuestionById(questionId);
        foundQuestion.setViews(foundQuestion.getViews() + 1);
        questionRepo.save(foundQuestion);

        // update user's viewed questions
        sessionUser.getQuestionViewedIds().add(questionId);
        userService.save(sessionUser);
    }
}
