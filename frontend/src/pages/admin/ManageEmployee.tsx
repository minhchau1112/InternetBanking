import React from 'react';
import { useState, useEffect } from 'react';
import { toast, ToastContainer } from "react-toastify";

type Employee = {
    id: number;
    name: string;
    status: string;
};

const ManageEmployee = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [currentEmployee, setCurrentEmployee] = useState<Employee | null>(null);
    const [formData, setFormData] = useState({ name: '', status: '' });

    useEffect(() => {
        fetchEmployees();
    }, []);

    const fetchEmployees = async () => {
        try {
            const response = await fetch('http://localhost:3306/api/employees');
            if (!response.ok) {
                throw new Error('Failed to fetch employees');
            }
            const data = await response.json();
            setEmployees(data);
        } catch (error) {
            // toast.error('Error fetching employees: ' + error.message);
            toast.error('Error fetching employees: ' + (error as any).message);
        }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const method = currentEmployee ? 'PUT' : 'POST';
        const url = currentEmployee ? `http://localhost:3306/api/employees/${currentEmployee.id}` : 'http://localhost:3306/api/employees';

        const body = currentEmployee 
            ? JSON.stringify({ name: formData.name, status: formData.status }) 
            : JSON.stringify({ name: formData.name, status: formData.status });

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body,
            });

            if (!response.ok) {
                throw new Error('Failed to save employee');
            }

            const savedEmployee = await response.json();
            toast.success(`Employee ${currentEmployee ? 'updated' : 'created'} successfully.`);
            fetchEmployees();
            setFormData({ name: '', status: '' });
            setCurrentEmployee(null);
        } catch (error) {
            toast.error('Error saving employee: ' + (error as any).message);
        }
    };

    const handleEdit = (employee: Employee) => {
        setCurrentEmployee(employee);
        setFormData({ name: employee.name, status: employee.status });
    };

    const handleDelete = async (id: number) => {
        try {
            const response = await fetch(`http://localhost:3306/api/employees/${id}`, { method: 'DELETE' });
            if (!response.ok) {
                throw new Error('Failed to delete employee');
            }
            toast.success('Employee deleted successfully.');
            fetchEmployees();
        } catch (error) {
            toast.error('Error deleting employee: ' + (error as any).message);
        }
    };

    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold mb-6">Admin Employee Management</h1>
            <form onSubmit={handleSubmit} className="mb-6">
                <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    placeholder="Name"
                    required
                    className="border p-2 mr-2"
                />
                <input
                    type="text"
                    name="status"
                    value={formData.status}
                    onChange={handleInputChange}
                    placeholder="Status"
                    required
                    className="border p-2 mr-2"
                />
                <button type="submit" className="bg-blue-600 text-white p-2">
                    {currentEmployee ? 'Update Employee' : 'Add Employee'}
                </button>
            </form>

            <ul>
                {employees.map(employee => (
                    <li key={employee.id} className="flex justify-between items-center mb-2">
                        <span>{employee.name} - {employee.status}</span>
                        <div>
                            <button onClick={() => handleEdit(employee)} className="bg-yellow-500 text-white p-1 mr-2">Edit</button>
                            <button onClick={() => handleDelete(employee.id)} className="bg-red-500 text-white p-1">Delete</button>
                        </div>
                    </li>
                ))}
            </ul>
            <ToastContainer />
        </div>
    );
};

export default ManageEmployee;
