package com.example.webproject.repositories.contracts;

import com.example.webproject.models.Post;
import com.example.webproject.models.Tag;

import java.util.List;

public interface TagRepository {

    Tag get(String name);

    Tag createTag(Tag tag);

}
