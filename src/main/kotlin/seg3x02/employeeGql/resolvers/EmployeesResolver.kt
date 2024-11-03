package seg3x02.employeeGql.resolvers

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.resolvers.types.CreateEmployeeInput
import java.util.*

@Controller
class EmployeesResolver(private val employeesRepository: EmployeesRepository) {

    @QueryMapping
    fun employees(): List<Employee> {
        return employeesRepository.findAll()
    }

    @QueryMapping
    fun employeeById(@Argument id: String): Employee? {
        return employeesRepository.findById(id).orElse(null)
    }

    @MutationMapping
    fun newEmployee(@Argument("createEmployeeInput") input: CreateEmployeeInput): Employee {
        val employee = Employee(
            name = input.name ?: throw Exception("Name is required"),
            dateOfBirth = input.dateOfBirth ?: throw Exception("Date of Birth is required"),
            city = input.city ?: throw Exception("City is required"),
            salary = input.salary ?: throw Exception("Salary is required"),
            gender = input.gender,
            email = input.email
        )
        employee.id = UUID.randomUUID().toString()
        return employeesRepository.save(employee)
    }

    @MutationMapping
    fun deleteEmployee(@Argument id: String): Boolean {
        return if (employeesRepository.existsById(id)) {
            employeesRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    @MutationMapping
    fun updateEmployee(@Argument id: String, @Argument("createEmployeeInput") input: CreateEmployeeInput): Employee? {
        val existingEmployee = employeesRepository.findById(id).orElse(null)
        if (existingEmployee != null) {
            val updatedEmployee = existingEmployee.copy(
                name = input.name ?: existingEmployee.name,
                dateOfBirth = input.dateOfBirth ?: existingEmployee.dateOfBirth,
                city = input.city ?: existingEmployee.city,
                salary = input.salary ?: existingEmployee.salary,
                gender = input.gender ?: existingEmployee.gender,
                email = input.email ?: existingEmployee.email
            )
            return employeesRepository.save(updatedEmployee)
        }
        return null
    }
}
