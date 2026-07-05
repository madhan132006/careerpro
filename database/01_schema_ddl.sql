-- =============================================================
-- AI Career Guidance Agent - Complete MySQL Database Schema
-- =============================================================

CREATE DATABASE IF NOT EXISTS careerpro_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE careerpro_db;

-- ---------------------------------------------------------------
-- 1. ROLES
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- 2. USERS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    email             VARCHAR(150) NOT NULL UNIQUE,
    password          VARCHAR(255) NOT NULL,
    full_name         VARCHAR(150),
    phone             VARCHAR(20),
    is_active         BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    role_id           BIGINT NOT NULL DEFAULT 1,
    otp               VARCHAR(10),
    otp_expiry        TIMESTAMP,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ---------------------------------------------------------------
-- 3. USER PROFILES
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_profiles (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT NOT NULL UNIQUE,
    dob               DATE,
    gender            VARCHAR(20),
    city              VARCHAR(100),
    state             VARCHAR(100),
    country           VARCHAR(100),
    college           VARCHAR(200),
    department        VARCHAR(100),
    degree            VARCHAR(100),
    graduation_year   INT,
    cgpa              DECIMAL(4,2),
    career_goal       TEXT,
    about_me          TEXT,
    linkedin_url      VARCHAR(255),
    github_url        VARCHAR(255),
    portfolio_url     VARCHAR(255),
    profile_photo_url VARCHAR(255),
    resume_url        VARCHAR(255),
    languages         VARCHAR(255),
    experience_years  INT DEFAULT 0,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- 4. SKILLS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS skills (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    category    VARCHAR(50) NOT NULL,  -- PROGRAMMING, DATABASE, CLOUD, AI, COMMUNICATION, LEADERSHIP, SOFT_SKILLS
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- 5. USER SKILLS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_skills (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    skill_name  VARCHAR(100) NOT NULL,
    category    VARCHAR(50) NOT NULL,
    proficiency INT NOT NULL DEFAULT 1 CHECK (proficiency BETWEEN 1 AND 5),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_skill (user_id, skill_name)
);

-- ---------------------------------------------------------------
-- 6. INTERESTS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_interests (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    interest_type   VARCHAR(50) NOT NULL,   -- TECHNICAL, CREATIVE, BUSINESS, RESEARCH, MANAGEMENT
    interest_value  VARCHAR(150) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- 7. CAREER TESTS (Assessment Questions)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS career_tests (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    question    TEXT NOT NULL,
    category    VARCHAR(50) NOT NULL,
    option_a    VARCHAR(255),
    option_b    VARCHAR(255),
    option_c    VARCHAR(255),
    option_d    VARCHAR(255),
    weight_a    INT DEFAULT 1,
    weight_b    INT DEFAULT 2,
    weight_c    INT DEFAULT 3,
    weight_d    INT DEFAULT 4,
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- 8. CAREER TEST RESULTS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS career_results (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    technical_score     INT DEFAULT 0,
    creative_score      INT DEFAULT 0,
    business_score      INT DEFAULT 0,
    research_score      INT DEFAULT 0,
    management_score    INT DEFAULT 0,
    total_score         INT DEFAULT 0,
    dominant_category   VARCHAR(50),
    test_date           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- 9. AI CAREER RECOMMENDATIONS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS career_recommendations (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    career_title        VARCHAR(150) NOT NULL,
    confidence_score    DECIMAL(5,2),
    reason              TEXT,
    required_skills     TEXT,
    missing_skills      TEXT,
    salary_range        VARCHAR(100),
    demand_level        VARCHAR(50),
    future_scope        TEXT,
    companies_hiring    TEXT,
    suggested_projects  TEXT,
    rank_order          INT DEFAULT 1,
    generated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- 10. COURSES
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS courses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    provider        VARCHAR(100),
    url             VARCHAR(500),
    type            VARCHAR(20) DEFAULT 'FREE',   -- FREE, PAID, YOUTUBE
    category        VARCHAR(100),
    skill_tag       VARCHAR(100),
    duration        VARCHAR(50),
    description     TEXT,
    rating          DECIMAL(3,2),
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- 11. LEARNING PATHS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS learning_paths (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    career_title        VARCHAR(150),
    roadmap_json        LONGTEXT,    -- JSON with week/month plan
    total_weeks         INT DEFAULT 12,
    current_week        INT DEFAULT 1,
    completion_percent  DECIMAL(5,2) DEFAULT 0,
    generated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- 12. RESUME ANALYSIS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS resume_analysis (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    file_name           VARCHAR(255),
    extracted_text      LONGTEXT,
    skills_found        TEXT,
    projects_found      TEXT,
    achievements_found  TEXT,
    ats_score           INT DEFAULT 0,
    formatting_score    INT DEFAULT 0,
    grammar_score       INT DEFAULT 0,
    overall_score       INT DEFAULT 0,
    improvement_tips    LONGTEXT,
    analyzed_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- 13. INTERVIEW QUESTIONS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS interview_questions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT,
    career_title    VARCHAR(150),
    question_type   VARCHAR(50),    -- TECHNICAL, HR, CODING, BEHAVIORAL, COMPANY_SPECIFIC
    question        TEXT NOT NULL,
    model_answer    TEXT,
    difficulty      VARCHAR(20) DEFAULT 'MEDIUM',
    generated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------
-- 14. NOTIFICATIONS
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    title       VARCHAR(200),
    message     TEXT,
    type        VARCHAR(50) DEFAULT 'INFO',  -- INFO, SUCCESS, WARNING, ERROR
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- 15. FEEDBACK
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS feedback (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    name        VARCHAR(150),
    email       VARCHAR(150),
    subject     VARCHAR(200),
    message     TEXT,
    rating      INT DEFAULT 5 CHECK (rating BETWEEN 1 AND 5),
    status      VARCHAR(20) DEFAULT 'OPEN',   -- OPEN, IN_PROGRESS, RESOLVED
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------
-- 16. JWT TOKENS (Blacklist for logout)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jwt_tokens (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    token           TEXT NOT NULL,
    user_id         BIGINT,
    expiry          TIMESTAMP,
    is_blacklisted  BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
