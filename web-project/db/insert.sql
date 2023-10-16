INSERT INTO forum.users (username, password, first_name, last_name, email, is_admin, phone_number, is_Blocked)
VALUES 
('johnDoe', 'johnPass123', 'John', 'Doe', 'john@example.com', 0, '123-456-7890', 0),
('janeDoe', 'janePass123', 'Jane', 'Doe', 'jane@example.com', 0, '098-765-4321', 0),
('adminUser', 'adminPass123', 'Admin', 'AdminPass', 'admin@example.com', 1, '111-222-3333', 0),
('aliceSmith', 'alicePass123', 'Alice', 'Smith', 'alice@example.com', 0, '555-555-5555', 0),
('bobBrown', 'bobPass123', 'Bob', 'Brown', 'bob@example.com', 0, '666-666-6666', 0);

INSERT INTO forum.posts (title, content, user_id)
VALUES 
('My first post', 'This is the content of my first post.', 1),
('A day in the life', 'Today was such an interesting day...', 2),
('Admin thoughts', 'Being an admin is cool.', 3),
('Tea over Coffee', 'Why I prefer tea over coffee.', 4),
('My favorite hobbies', 'Here are some of my hobbies.', 5);

INSERT INTO forum.comments (content, post_id, user_id)
VALUES 
('Great post!', 1, 2),
('I totally agree with you.', 2, 1),
('Admins have tough jobs!', 3, 4),
('Coffee is better!', 4, 5),
('Same here!', 5, 3);

INSERT INTO forum.liked_posts (post_id, user_id)
VALUES 
(1, 2),
(2, 1),
(3, 5),
(4, 3),
(5, 2);




