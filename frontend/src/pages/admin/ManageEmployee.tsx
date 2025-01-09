import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { toast, ToastContainer } from "react-toastify";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Drawer from 'react-modern-drawer';
import 'react-modern-drawer/dist/index.css';
import { FaEdit, FaTrash } from 'react-icons/fa';
import { AppDispatch, RootState } from '@/redux/store';
import { useDispatch, useSelector } from 'react-redux';
import { Chip, Box } from '@mui/material';

type Employee = {
    id: number;
    name: string;
    status: string;
};

// Function to get styles based on status
const getStatusStyles = (status: string) => {
    switch (status) {
        case 'ACTIVE':
            return {
                backgroundColor: '#ECFDF3',
                color: '#079455',
                borderColor: '#ABEFC6',
            };
        case 'LOCKED':
            return {
                backgroundColor: '#FEF3F2',
                color: '#D92D20',
                borderColor: '#FECDCA',
            };
        default:
            return {
                backgroundColor: '#E0E0E0',
                color: '#757575',
            };
    }
};

const ManageEmployee: React.FC = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [currentEmployee, setCurrentEmployee] = useState<Employee | null>(null);
    const [formData, setFormData] = useState<{ id: number; name: string; userName: string; status: string }>({ id: 0, name: '', userName: '', status: '' });
    const [searchTerm, setSearchTerm] = useState<string>('');
    const [filteredEmployees, setFilteredEmployees] = useState<Employee[]>([]);
    const [isDrawerOpen, setIsDrawerOpen] = useState<boolean>(false);

    const dispatch = useDispatch<AppDispatch>();
    const accessToken = localStorage.getItem('access_token');

    const fetchEmployees = async () => {
        try {
            const response = await axios.get('http://localhost:8888/api/employees', {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });
            setEmployees(response.data.data);
            setFilteredEmployees(response.data.data);
            console.log("Employees: ", response.data);
        } catch (error) {
            console.error("Error fetching employees:", error);
            toast.error('Error fetching employees: ' + (error as Error).message);
        }
    };

    useEffect(() => {
        fetchEmployees();
    }, [accessToken]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const method = currentEmployee ? 'PUT' : 'POST';
        const url = currentEmployee ? `http://localhost:8888/api/employees/${currentEmployee.id}` : 'http://localhost:8888/api/employees';

        const body = JSON.stringify(currentEmployee ? { id: formData.id, name: formData.name, status: formData.status } : { name: formData.name, userName: formData.userName, status: formData.status });

        try {
            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': "Bearer " + accessToken
                },
                body,
            });

            if (!response.ok) {
                throw new Error('Failed to save employee');
            }

            const savedEmployee = await response.json();
            if (!currentEmployee) {
                toast.success('New employee added successfully!');
            } else {
                toast.success(`Employee updated successfully.`);
            }

            fetchEmployees();
            setFormData({ id: 0, name: '', userName: '', status: '' });
            setCurrentEmployee(null);
            closeDrawer();
        } catch (error) {
            toast.error('Error saving employee: ' + (error as Error).message);
        }
    };

    const handleEdit = (employee: Employee) => {
        console.log("on handle Edit");
        setIsDrawerOpen(true);
        console.log("setIsDrawerOpen = true");

        setCurrentEmployee(employee);
        setFormData({ id: employee.id, name: employee.name, userName: '', status: employee.status });
        // setIsDrawerOpen(true);
    };

    const handleDelete = async (id: number) => {
        const url = `http://localhost:8888/api/employees/${id}`;

        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`,
                },
            });

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
        setCurrentEmployee(null);
        setFormData({ id: 0, name: '', userName: '', status: '' });
    };

    const closeDrawer = () => {
        setIsDrawerOpen(false);
    };

    const renderStatus = (status: string) => {
        const styles = getStatusStyles(status);
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
                <Chip
                    label={status}
                    size="small"
                    sx={{
                        fontWeight: 'bold',
                        textTransform: 'capitalize',
                        border: '1px solid',
                        paddingX: 1.5,
                        paddingY: 1,
                        ...styles,
                    }}
                />
            </Box>
        );
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
                    {filteredEmployees.map(employee => (
                        <tr key={employee.id}>
                            <td className="border p-2">{employee.id}</td>
                            <td className="border p-2">{employee.name}</td>
                            <td className="border p-2">{renderStatus(employee.status)}</td>
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
            <Drawer open={isDrawerOpen} onClose={closeDrawer} direction="right"> {/* Add direction prop */}
                <div className="p-4">
                    <h2 className="text-xl mb-4">{currentEmployee ? 'Edit Employee' : 'Add Employee'}</h2>
                    <form onSubmit={handleSubmit}>
                        {/* <div className="mb-4">
                        <label htmlFor="id" className="block mb-1">ID</label>
                        <Input
                            type="number"
                            name="id"
                            value={formData.id}
                            onChange={handleInputChange}
                            required
                            className="border p-2 w-full"
                        />
                    </div> */}
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
                            <label htmlFor="userName" className="block mb-1">Username</label>
                            <Input
                                type="text"
                                name="userName"
                                value={formData.userName}
                                onChange={handleInputChange}
                                required
                                className="border p-2 w-full"
                            />
                        </div>
                        {/* <div className="mb-4">
                            <label htmlFor="status" className="block mb-1">Status</label>
                                <Input
                                    type="text"
                                    name="status"
                                    value={formData.status}
                                    onChange={handleInputChange}
                                    required
                                    className="border p-2 w-full"
                                />
                        </div> */}
                        <div className="mb-4">
                            <label className="block mb-1">Status</label>
                            <div className="flex items-center">
                                <label className="mr-4">
                                    <input
                                        type="radio"
                                        name="status"
                                        value="ACTIVE"
                                        checked={formData.status === 'ACTIVE'}
                                        onChange={handleInputChange}
                                        required
                                        className="mr-1"
                                    />
                                    ACTIVE
                                </label>
                                <label>
                                    <input
                                        type="radio"
                                        name="status"
                                        value="LOCKED"
                                        checked={formData.status === 'LOCKED'}
                                        onChange={handleInputChange}
                                        required
                                        className="mr-1"
                                    />
                                    LOCKED
                                </label>
                            </div>
                        </div>
                        <div className="flex justify-end">
                            <Button onClick={closeDrawer} variant="secondary">Cancel</Button>
                            <Button type="submit" variant="default" className="mr-2">
                                {currentEmployee ? 'Update Employee' : 'Add Employee'}
                            </Button>
                        </div>
                    </form>
                </div>
            </Drawer>
        </div>
    );
};

export default ManageEmployee;
