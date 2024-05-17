package com.devcommunity.infyStack.services;

import com.devcommunity.infyStack.models.entities.Tag;
import com.devcommunity.infyStack.repositories.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepo;

    public void save(Tag tag){

        // sanitize tag name
        String tagName = tag.getName();
        tag.setName(
                tagName.strip()
                .replaceAll(" ", "")
                .toLowerCase()
        );

        tagRepo.save(tag);
    }

    public Tag getTagByName(String name){

        return tagRepo.findByName(
                name.strip()
                .replaceAll(" ", "")
                .toLowerCase()
        );
    }

}
