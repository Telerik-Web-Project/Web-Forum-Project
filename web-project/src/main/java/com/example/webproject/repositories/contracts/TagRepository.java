package com.example.webproject.repositories.contracts;

import com.example.webproject.models.Tag;

public interface TagRepository {

    Tag get(String name);

    Tag createTag(Tag tag);
}
