import React, { useState, useEffect } from 'react';
import { toast, ToastContainer } from "react-toastify";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Drawer from 'react-modern-drawer';
import 'react-modern-drawer/dist/index.css';
import { FaEdit, FaTrash } from 'react-icons/fa';

type Employee = {
    id: number;
    name: string;
    status: string;
};

const ManageEmployee: React.FC = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [currentEmployee, setCurrentEmployee] = useState<Employee | null>(null);
    const [formData, setFormData] = useState<{ id: number; name: string; status: string }>({ id: 0, name: '', status: '' });
    const [searchTerm, setSearchTerm] = useState<string>('');
    const [filteredEmployees, setFilteredEmployees] = useState<Employee[]>([]);
    const [isDrawerOpen, setIsDrawerOpen] = useState<boolean>(false);

    useEffect(() => {
        fetchEmployees();
    }, []);

    const fetchEmployees = async () => {
        try {
            const response = await fetch('http://localhost:8888/api/employees');
            if (!response.ok) {
                throw new Error('Failed to fetch employees');
            }
            const data = await response.json();
            setEmployees(data);
            setFilteredEmployees(data);
        } catch (error) {
            toast.error('Error fetching employees: ' + (error as Error).message);
        }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const method = currentEmployee ? 'PUT' : 'POST';
        const url = currentEmployee ? `http://localhost:8888/api/employees/${currentEmployee.id}` : 'http://localhost:3306/api/employees';

        const body = JSON.stringify({ id: formData.id, name: formData.name, status: formData.status });

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
            setFormData({ id: 0, name: '', status: '' });
            setCurrentEmployee(null);
            closeDrawer(); // Close drawer after submission
        } catch (error) {
            toast.error('Error saving employee: ' + (error as Error).message);
        }
    };

    const handleEdit = (employee: Employee) => {
        setCurrentEmployee(employee);
        setFormData({ id: employee.id, name: employee.name, status: employee.status });
        setIsDrawerOpen(true);
    };

    const handleDelete = async (id: number) => {
        try {
            const response = await fetch(`http://localhost:8888/api/employees/${id}`, { method: 'DELETE' });
            if (!response.ok) {
                throw new Error('Failed to delete employee');
            }
            toast.success('Employee deleted successfully.');
            fetchEmployees();
        } catch (error) {
            toast.error('Error deleting employee: ' + (error as Error).message);
        }
    };

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const term = e.target.value;
        setSearchTerm(term);
        const filtered = employees.filter(employee => employee.name.toLowerCase().includes(term.toLowerCase()));
        setFilteredEmployees(filtered);
    };

    const handleAddEmployee = () => {
        setIsDrawerOpen(true);
        setFormData({ id: 0, name: '', status: '' }); // Reset form data for new employee
    };

    const closeDrawer = () => {
        setIsDrawerOpen(false);
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
                <Button onClick={handleAddEmployee} variant="default">
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
                                <Button onClick={() => handleEdit(employee)} variant="secondary" className="mr-2">
                                    <FaEdit className="inline mr-1" /> Edit
                                </Button>
                                <Button onClick={() => handleDelete(employee.id)} variant="destructive">
                                    <FaTrash className="inline mr-1" /> Delete
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <ToastContainer />
            <Drawer isOpen={isDrawerOpen} onClose={closeDrawer}>
                <div className="p-4">
                    <h2 className="text-xl mb-4">{currentEmployee ? 'Edit Employee' : 'Add Employee'}</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="mb-4">
                            <label htmlFor="id" className="block mb-1">ID</label>
                            <Input
                                type="number"
                                name="id"
                                value={formData.id}
                                onChange={handleInputChange}
                                required
                                className="border p-2 w-full"
                            />
                        </div>
                        <div className="mb-4">
                            <label htmlFor="name" className="block mb-1">Name</label>
                            <Input
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleInputChange}
                                required
                                className="border p-2 w-full"
                            />
                        </div>
                        <div className="mb-4">
                            <label htmlFor="status" className="block mb-1">Status</label>
                            <Input
                                type="text"
                                name="status"
                                value={formData.status}
                                onChange={handleInputChange}
                                required
                                className="border p-2 w-full"
                            />
                        </div>
                        <div className="flex justify-end">
                            <Button type="submit" variant="default" className="mr-2">
                                {currentEmployee ? 'Update Employee' : 'Add Employee'}
                            </Button>
                            <Button onClick={closeDrawer} variant="destructive">Cancel</Button>
                        </div>
                    </form>
                </div>
            </Drawer>
        </div>
    );
};

export default ManageEmployee;
