import React from 'react';
import { useState, useEffect } from 'react';
import { toast, ToastContainer } from "react-toastify";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

type Employee = {
    id: number;
    name: string;
    status: string;
};

const ManageEmployee = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [currentEmployee, setCurrentEmployee] = useState<Employee | null>(null);
    const [formData, setFormData] = useState({ name: '', status: '' });
    const [searchTerm, setSearchTerm] = useState('');
    const [filteredEmployees, setFilteredEmployees] = useState<Employee[]>([]);
    
    
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
            setFilteredEmployees(data);
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

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const term = e.target.value;
        setSearchTerm(term);
        const filtered = employees.filter(employee => employee.name.toLowerCase().includes(term.toLowerCase()));
        setFilteredEmployees(filtered);
    };

    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold mb-6">Employee Management</h1>
            <div className="mb-4 flex justify-between items-center">
                <Input
                    type="text"
                    placeholder="Search by name"
                    value={searchTerm}
                    onChange={handleSearchChange}
                    className="mr-2"
                />
                <Button onClick={() => setCurrentEmployee(null)} variant="default">
                    Add Employee
                </Button>
            </div>
            <table className="min-w-full border">
                <thead>
                    <tr>
                        <th className="border p-2">ID</th>
                        <th className="border p-2">Name</th>
                        <th className="border p-2">Status</th>
                        <th className="border p-2">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredEmployees.sort((a, b) => a.id - b.id).map(employee => (
                        <tr key={employee.id}>
                            <td className="border p-2">{employee.id}</td>
                            <td className="border p-2">{employee.name}</td>
                            <td className="border p-2">{employee.status}</td>
                            <td className="border p-2">
                                <Button onClick={() => handleEdit(employee)} variant="secondary" className="mr-2">Edit</Button>
                                <Button onClick={() => handleDelete(employee.id)} variant="destructive">Delete</Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <ToastContainer />
        </div>
    );
};

export default ManageEmployee;
