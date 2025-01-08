import React from 'react';
import { useState, useEffect } from 'react';
import { toast, ToastContainer } from "react-toastify";

type Employee = {
    id: number;
    name: string;
    status: string;
};

const ManageEmployee = () => {
    
    return (
        <div className="w-[calc(100vw-256px)] max-w-full h-screen flex items-center justify-center p-8 bg-gray-100 ">
            <div className="w-full bg-white p-8 rounded-lg shadow-md">
                <h1 className="text-3xl font-bold mb-6 text-gray-800 text-left">All Employees</h1>
            </div>
            
        </div>
    );
};

export default ManageEmployee;