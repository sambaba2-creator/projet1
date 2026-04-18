-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('ADMIN', 'SCHEDULE_MANAGER', 'TEACHER', 'STUDENT')),
    active BOOLEAN DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create Buildings Table
CREATE TABLE IF NOT EXISTS buildings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    location TEXT NOT NULL,
    floors INTEGER NOT NULL CHECK(floors > 0),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create Equipments Table
CREATE TABLE IF NOT EXISTS equipments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    number TEXT NOT NULL,
    capacity INTEGER NOT NULL CHECK(capacity > 0),
    type TEXT NOT NULL CHECK(type IN ('TD', 'TP', 'AMPHI', 'SEMINAR', 'CONFERENCE')),
    building_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (building_id) REFERENCES buildings(id),
    UNIQUE(number, building_id)
);

-- Create Room_Equipment Junction Table
CREATE TABLE IF NOT EXISTS room_equipment (
    room_id INTEGER NOT NULL,
    equipment_id INTEGER NOT NULL,
    PRIMARY KEY (room_id, equipment_id),
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipments(id) ON DELETE CASCADE
);

-- Create Classes Table
CREATE TABLE IF NOT EXISTS classes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    capacity INTEGER NOT NULL CHECK(capacity > 0),
    level TEXT NOT NULL,
    specialization TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create Courses Table
CREATE TABLE IF NOT EXISTS courses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    subject TEXT NOT NULL,
    teacher_id INTEGER NOT NULL,
    class_id INTEGER NOT NULL,
    day_of_week TEXT NOT NULL CHECK(day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    start_time TEXT NOT NULL,
    end_time TEXT,
    duration INTEGER NOT NULL,
    room_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Create Reservations Table
CREATE TABLE IF NOT EXISTS reservations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_id INTEGER NOT NULL,
    date TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    type TEXT NOT NULL CHECK(type IN ('COURSE', 'EVENT')),
    course_id INTEGER,
    user_id INTEGER NOT NULL,
    reason TEXT,
    status TEXT DEFAULT 'CONFIRMED' CHECK(status IN ('CONFIRMED', 'PENDING', 'CANCELLED')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    message TEXT NOT NULL,
    type TEXT NOT NULL,
    read BOOLEAN DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create Conflict Logs Table
CREATE TABLE IF NOT EXISTS conflict_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_id INTEGER,
    teacher_id INTEGER,
    class_id INTEGER,
    conflict_type TEXT NOT NULL CHECK(conflict_type IN ('ROOM', 'TEACHER', 'CLASS')),
    description TEXT NOT NULL,
    resolved BOOLEAN DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (class_id) REFERENCES classes(id)
);

-- Create Usage Statistics Table
CREATE TABLE IF NOT EXISTS usage_statistics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_id INTEGER NOT NULL,
    date TEXT NOT NULL,
    hours_used REAL NOT NULL,
    total_available_hours REAL NOT NULL,
    occupancy_rate REAL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    UNIQUE(room_id, date)
);

-- Create Indexes for Better Performance
CREATE INDEX IF NOT EXISTS idx_courses_teacher ON courses(teacher_id);
CREATE INDEX IF NOT EXISTS idx_courses_class ON courses(class_id);
CREATE INDEX IF NOT EXISTS idx_courses_room ON courses(room_id);
CREATE INDEX IF NOT EXISTS idx_courses_day ON courses(day_of_week);
CREATE INDEX IF NOT EXISTS idx_reservations_room ON reservations(room_id);
CREATE INDEX IF NOT EXISTS idx_reservations_date ON reservations(date);
CREATE INDEX IF NOT EXISTS idx_reservations_user ON reservations(user_id);
CREATE INDEX IF NOT EXISTS idx_reservations_course ON reservations(course_id);
CREATE INDEX IF NOT EXISTS idx_rooms_building ON rooms(building_id);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);

-- Insert Sample Data (Optional)
-- Admin User
INSERT OR IGNORE INTO users (name, email, password, role) 
VALUES ('Admin', 'admin@university.edu', 'admin123', 'ADMIN');

-- Schedule Manager User
INSERT OR IGNORE INTO users (name, email, password, role)
VALUES ('Gestionnaire EDT', 'manager@university.edu', 'manager123', 'SCHEDULE_MANAGER');

-- Sample Buildings
INSERT OR IGNORE INTO buildings (name, location, floors)
VALUES 
    ('Bâtiment Informatique', 'Campus Nord', 3),
    ('Bâtiment Sciences', 'Campus Sud', 4),
    ('Bâtiment Lettres', 'Campus Centre', 2);

-- Sample Equipments
INSERT OR IGNORE INTO equipments (name)
VALUES 
    ('Vidéoprojecteur'),
    ('Tableau interactif'),
    ('Climatisation'),
    ('Système audio'),
    ('Caméra');

-- Sample Rooms
INSERT OR IGNORE INTO rooms (number, capacity, type, building_id)
VALUES 
    ('A101', 40, 'TD', 1),
    ('A102', 30, 'TP', 1),
    ('A103', 200, 'AMPHI', 1),
    ('B101', 50, 'TD', 2),
    ('B102', 60, 'AMPHI', 2),
    ('C101', 35, 'TD', 3);

-- Sample Classes
INSERT OR IGNORE INTO classes (name, capacity, level, specialization)
VALUES 
    ('L3 Informatique', 40, 'Licence', 'Informatique'),
    ('M1 Informatique', 25, 'Master', 'Informatique'),
    ('L2 Sciences', 50, 'Licence', 'Sciences'),
    ('L1 Lettres', 30, 'Licence', 'Lettres et Humanités');

-- Sample Teacher Users
INSERT OR IGNORE INTO users (name, email, password, role)
VALUES 
    ('M. Ndiaye', 'ndiaye@university.edu', 'pass123', 'TEACHER'),
    ('Mme Ba', 'ba@university.edu', 'pass123', 'TEACHER'),
    ('M. Diallo', 'diallo@university.edu', 'pass123', 'TEACHER');

-- Sample Students
INSERT OR IGNORE INTO users (name, email, password, role)
VALUES 
    ('Student 1', 'student1@university.edu', 'pass123', 'STUDENT'),
    ('Student 2', 'student2@university.edu', 'pass123', 'STUDENT'),
    ('Student 3', 'student3@university.edu', 'pass123', 'STUDENT');
