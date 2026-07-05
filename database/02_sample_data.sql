USE careerpro_db;

-- ---------------------------------------------------------------
-- Roles Sample Data
-- ---------------------------------------------------------------
INSERT IGNORE INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- ---------------------------------------------------------------
-- Sample Career Test Questions
-- ---------------------------------------------------------------
INSERT IGNORE INTO career_tests (question, category, option_a, option_b, option_c, option_d, weight_a, weight_b, weight_c, weight_d) VALUES
('I enjoy solving complex logical problems and puzzles.', 'TECHNICAL', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4),
('I like designing and creating visual content.', 'CREATIVE', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4),
('I am comfortable leading a team towards a goal.', 'MANAGEMENT', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4),
('I enjoy reading research papers and doing in-depth analysis.', 'RESEARCH', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4),
('I enjoy negotiating and closing deals.', 'BUSINESS', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4),
('I prefer building applications or writing code.', 'TECHNICAL', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4),
('I enjoy brainstorming creative marketing ideas.', 'CREATIVE', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4),
('I like organizing people and resources to achieve objectives.', 'MANAGEMENT', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4),
('I am fascinated by how things work at a scientific level.', 'RESEARCH', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4),
('I find entrepreneurship and startups exciting.', 'BUSINESS', 'Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree', 1, 2, 3, 4);

-- ---------------------------------------------------------------
-- Sample Courses
-- ---------------------------------------------------------------
INSERT IGNORE INTO courses (title, provider, url, type, category, skill_tag, duration, description, rating) VALUES
('Python for Everybody', 'Coursera (University of Michigan)', 'https://www.coursera.org/specializations/python', 'FREE', 'Programming', 'Python', '8 months', 'Comprehensive Python programming course covering fundamentals to data structures.', 4.8),
('Full Stack Web Development', 'freeCodeCamp', 'https://www.freecodecamp.org', 'FREE', 'Web Development', 'JavaScript', '6 months', 'Complete web development curriculum including HTML, CSS, JavaScript, and Node.js.', 4.7),
('Machine Learning Specialization', 'Coursera (Stanford)', 'https://www.coursera.org/specializations/machine-learning-introduction', 'PAID', 'AI/ML', 'Machine Learning', '3 months', 'Comprehensive machine learning course by Andrew Ng.', 4.9),
('AWS Certified Solutions Architect', 'AWS', 'https://aws.amazon.com/training/', 'PAID', 'Cloud', 'AWS', '4 months', 'Official AWS certification training for cloud architecture.', 4.8),
('Data Structures and Algorithms', 'YouTube (Abdul Bari)', 'https://www.youtube.com/watch?v=0IAPZzGSbME', 'YOUTUBE', 'DSA', 'Algorithms', '12 hours', 'Complete DSA course covering all major algorithms and data structures.', 4.9),
('Spring Boot Microservices', 'Udemy', 'https://www.udemy.com/course/microservices-with-spring-boot-and-spring-cloud/', 'PAID', 'Backend', 'Spring Boot', '20 hours', 'Build production-ready microservices with Spring Boot and Spring Cloud.', 4.7),
('React JS Complete Guide', 'YouTube (Traversy Media)', 'https://www.youtube.com/watch?v=w7ejDZ8SWv8', 'YOUTUBE', 'Frontend', 'React', '9 hours', 'Complete React crash course covering hooks, context API, and routing.', 4.8),
('SQL and Database Design', 'freeCodeCamp', 'https://www.freecodecamp.org/learn/relational-database/', 'FREE', 'Database', 'SQL', '3 months', 'Comprehensive relational database and SQL course.', 4.6),
('Docker and Kubernetes', 'Udemy', 'https://www.udemy.com/course/docker-and-kubernetes-the-complete-guide/', 'PAID', 'DevOps', 'Docker', '22 hours', 'Complete Docker and Kubernetes guide for DevOps engineers.', 4.8),
('Communication and Leadership', 'Coursera', 'https://www.coursera.org/learn/communication-skills', 'FREE', 'Soft Skills', 'Communication', '4 weeks', 'Develop essential communication and leadership skills for the workplace.', 4.5);

-- ---------------------------------------------------------------
-- Admin user (password: Admin@123)
-- ---------------------------------------------------------------
INSERT IGNORE INTO users (email, password, full_name, phone, is_active, is_email_verified, role_id)
VALUES ('admin@careerpro.ai', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LvlsiAg87re', 'Super Admin', '9999999999', TRUE, TRUE, 2);
