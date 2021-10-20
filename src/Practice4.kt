import java.time.LocalDate
import java.time.Period

fun main(){
    val univer = DataSource.university

    println("Средняя оценка по университету: ${"%.2f".format(univer.averageGrade)}")

    println("Студенты на первом курсе:")
    univer.courses.filterKeys { it == 1 }.forEach{println("${it.value}")}

    println("Студенты на втором курсе:")
    univer.courses.filterKeys { it == 2 }.forEach{println("${it.value}")}

    println("Студенты на третьем курсе:")
    univer.courses.filterKeys { it == 3 }.forEach{println("${it.value}")}

    println("Студенты на четвертом курсе:")
    univer.courses.filterKeys { it == 4 }.forEach{println("${it.value}")}

    DataSource.addStudent(univer,Student("Иван Потапкин", LocalDate.of(2004, 9, 15), listOf(StudyProgram.Math withGrade 3.8, StudyProgram.Prgrm withGrade 4.2)))
}
data class Subject(val title: String, val grade: Double)

data class Student(val name: String, val dateBirth: LocalDate, val subjects: List<Subject>){
    val averageGrade: Double
        get() = subjects.average{ it.grade }
    val age = (Period.between(LocalDate.now(), dateBirth).years).toString().trimMargin("-").toInt()
}

data class University(val title: String, val students: MutableList<Student>){
    val averageGrade: Double
        get() = students.average{ it.averageGrade }

    val courses: Map<Int, List<Student>>
        get() = students
            .groupBy{ it.age }
            .mapKeys {
                when (it.key) {
                    17 -> 1
                    18 -> 2
                    19 -> 3
                    20 -> 4
                    else -> throw StudentException()
                }
            }
}

class StudentException : Throwable(){
    override val message: String = "Student Too Young OR Old"
}

enum class StudyProgram(private val title: String){
    Math("Математика"),
    Rus("Русский язык"),
    Prgrm("Программирование");

    infix fun withGrade(grade: Double) = Subject(title, grade)
}

typealias StudentsListener = (Student) -> Unit

object DataSource{
    val university : University by lazy{ University("univer", mutableListOf(
        Student("Игорь Коршев", LocalDate.of(2001, 9, 15), listOf(StudyProgram.Math withGrade 2.8, StudyProgram.Rus withGrade 3.2)),
        Student("Иван Иванов", LocalDate.of(2001, 8, 16), listOf(StudyProgram.Math withGrade 2.5, StudyProgram.Prgrm withGrade 3.8)),
        Student("Петр Петров", LocalDate.of(2002, 7, 17), listOf(StudyProgram.Rus withGrade 3.2, StudyProgram.Prgrm withGrade 3.7)),
        Student("Николай Сидоркин", LocalDate.of(2002, 6, 18), listOf(StudyProgram.Math withGrade 3.5, StudyProgram.Rus withGrade 4.8)),
        Student("Олег Антонов", LocalDate.of(2003, 5, 19), listOf(StudyProgram.Prgrm withGrade 4.5, StudyProgram.Math withGrade 3.5)),
        Student("Антон Казанин", LocalDate.of(2003, 4, 20), listOf(StudyProgram.Prgrm withGrade 3.9, StudyProgram.Rus withGrade 4.2)),
        Student("Никита Рыжов", LocalDate.of(2004, 3, 21), listOf(StudyProgram.Math withGrade 3.6, StudyProgram.Prgrm withGrade 4.6)))
        )
    }
    private val onNewStudentListener: StudentsListener? = { println("Добавлен новый студент ${it.name} со средней оценкой ${it.averageGrade}") }

    fun addStudent(university: University, student: Student){
        if(university.students.add(student))
            onNewStudentListener?.let { it(student) }
    }
}

fun <T> Iterable<T>.average(block: (T) -> Double): Double{
    var sum = 0.0
    var i = 0
    for(elem in this){
        sum += block(elem)
        i++
    }
    return sum/i
}