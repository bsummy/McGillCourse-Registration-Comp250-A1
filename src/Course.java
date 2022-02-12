public class Course {
    public String code;
    public int capacity;
    public SLinkedList<Student>[] studentTable;
    public int size;
    public SLinkedList<Student> waitlist;

    public Course(String code) {
        this.code = code;
        this.studentTable = new SLinkedList[10];
        this.size = 0;
        this.waitlist = new SLinkedList();
        this.capacity = 10;
    }

    public Course(String code, int capacity) {
        this.code = code;
        this.studentTable = new SLinkedList[capacity];
        this.size = 0;
        this.waitlist = new SLinkedList();
        this.capacity = capacity;
    }

    public void changeArrayLength(int m) {
        SLinkedList<Student>[] expStudentTable = new SLinkedList[m];
        Student[] studentArray = this.getRegisteredStudents();
        for (int i = 0; i < this.getRegisteredStudents().length; i++) {
            Student student = studentArray[i];
            if (student == null){
                continue;
            }
            this.remove(student.id);

            int stuTableSlot = student.id % m;
            if (expStudentTable[stuTableSlot] == null) {
                expStudentTable[stuTableSlot] = new SLinkedList();
            }
            expStudentTable[stuTableSlot].addLast(student);
            student.addCourse(this.code);
        }

        this.studentTable = expStudentTable;
        int waitlistSize = this.waitlist.size();
        for (int j = 0; j < waitlistSize; j++) {
            Student student = this.waitlist.removeFirst();
            if (student == null) {
                continue;
            }
            this.put(student);
        }
        this.waitlist = new SLinkedList();


    }

    public boolean put(Student s) {
        if (s.isRegisteredOrWaitlisted(this.code)) {
            return false;
        } else if (s.courseCodes.size() >= 3) {
            return false;
        } else {
            if (this.size < this.capacity) {
                int stuTableSlot = s.id % this.capacity;
                if (this.studentTable[stuTableSlot] == null) {
                    this.studentTable[stuTableSlot] = new SLinkedList();
                }

                this.studentTable[stuTableSlot].addLast(s);
                s.addCourse(this.code);
                this.size++;
            } else if (this.size >= this.capacity && this.waitlist.size() >= this.capacity/2) {
                //this needs to be increased by 50 percent
                int m = this.capacity + this.capacity/2;
                this.changeArrayLength(m);
                this.size = m;
                this.capacity = m;
                this.waitlist.addLast(s);
                s.addCourse(this.code);
            } else if (this.size >= this.capacity) {
                this.waitlist.addLast(s);
                s.addCourse(this.code);

            }
            return true;
        }
    }

    public Student get(int id) {
        int[] regIdArray = this.getRegisteredIDs();
        int[] waitIdArray = this.getWaitlistedIDs();
        Student[] StudentArray = null;
        int regIdArrayLength = regIdArray.length;
        int waitIdArrayLength = waitIdArray.length;

        int regId;
        for(int i = 0; i < regIdArrayLength; i++) {
            regId = regIdArray[i];
            if (regId == id) {
                StudentArray = this.getRegisteredStudents();
                break;
            }
        }

        if (StudentArray == null) {
            int waitId;
            for(int i = 0; i < waitIdArrayLength; i++) {
                waitId = waitIdArray[i];
                if (waitId == id) {
                    StudentArray = this.getWaitlistedStudents();
                    break;
                }
            }
        }

        if (StudentArray == null){
            return null;
        }

        for(int i = 0; i < StudentArray.length; ++i) {
            Student student = StudentArray[i];
            if (student.id == id) {
                return student;
            }
        }

        return null;
    }

    public Student remove(int id) {
        Student[] regStudentArray = this.getRegisteredStudents();
        Student[] waitStudentArray = this.getWaitlistedStudents();
        Student studentObj = null;
        int studentTableNode = 0;
        SLinkedList<Student> listNode = null;
        boolean regStudentRemoved = true;

        for(int i = 0; i < regStudentArray.length; i++) {
            Student student = regStudentArray[i];
            if (student != null) {
                if (student.id == id) {
                    studentObj = student;
                    regStudentRemoved = true;
                }
            }
        }

        for(int i = 0; i < waitStudentArray.length; i++) {
            Student student = waitStudentArray[i];
            if (student != null) {
                if (student.id == id) {
                    studentObj = student;
                    regStudentRemoved = false;
                }
            }
        }

        if (studentObj == null){
            return studentObj;
        }

        for(int i = 0; i < this.capacity; i++) {
            listNode = this.studentTable[studentTableNode];
            studentTableNode++;
            if (listNode == null) {
                continue;
            }
            for (int j = 0; j < listNode.size(); j++) {
                if (listNode.get(j) == studentObj) {
                    listNode.remove(studentObj);
                    studentObj.dropCourse(this.code);
                    this.size--;
                    if (regStudentRemoved && this.waitlist.getFirst() != null){
                        this.put(this.waitlist.removeFirst());

                        this.size++;
                    }
                    return studentObj;
                }
            }
        }

        for(int i = 0; i < this.capacity; i++) {
            if (this.waitlist.get(i) == studentObj) {
                this.waitlist.remove(studentObj);
                studentObj.dropCourse(this.code);
                return studentObj;
            }
        }

        return null;
    }

    public int getCourseSize() {
        return this.size;
    }

    public int[] getRegisteredIDs() {
        Student[] regStudentArray = this.getRegisteredStudents();
        int[] intArray = new int[this.capacity];
        int regStudentArrayLength = regStudentArray.length;

        for(int i = 0; i < regStudentArrayLength; ++i) {
            Student student = regStudentArray[i];
            if (student == null){
                continue;
            }
            intArray[i] = student.id;
        }

        return intArray;
    }

    public Student[] getRegisteredStudents() {
        Student[] studentArray = new Student[this.capacity];
        int counter = 0;
        SLinkedList<Student> listNode = null;

        for(int i = 0; i < this.capacity; i++) {
            listNode = this.studentTable[i];
            if (listNode != null) {
                for(int j = 0; j < listNode.size(); j++) {
                    studentArray[counter] = (Student) listNode.get(j);
                    counter++;
                }
            }
        }
        return studentArray;
    }

    public int[] getWaitlistedIDs() {
        int[] intArray = new int[this.capacity / 2];
        Student[] waitStudentArray = this.getWaitlistedStudents();
        int waitStudentArrayLength = intArray.length;

        for(int i = 0; i < waitStudentArrayLength; i++) {
            Student student = waitStudentArray[i];
            if (student == null){
                continue;
            }
            intArray[i] = student.id;
        }

        return intArray;
    }

    public Student[] getWaitlistedStudents() {
        Student[] waitStudentArray = new Student[this.capacity / 2];

        for(int j = 0; j < this.waitlist.size(); ++j) {
            waitStudentArray[j] = (Student) this.waitlist.get(j);
        }
        return waitStudentArray;
    }

    public String toString() {
        String s = "Course: " + this.code + "\n";
        s = s + "--------\n";

        for(int i = 0; i < this.studentTable.length; ++i) {
            s = s + "|" + i + "     |\n";
            s = s + "|  ------> ";
            SLinkedList<Student> list = this.studentTable[i];
            if (list != null) {
                for(int j = 0; j < list.size(); ++j) {
                    Student student = (Student)list.get(j);
                    s = s + student.id + ": " + student.name + " --> ";
                }
            }

            s = s + "\n--------\n\n";
        }

        return s;
    }
}
