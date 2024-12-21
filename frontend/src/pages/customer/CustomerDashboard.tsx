import React from "react";

const CustomerDashboard = () => {
  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <h1 className="text-2xl font-bold mb-4">Manage Bank Account</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h2 className="text-lg font-semibold mb-2">My Card</h2>
          <div className="space-y-4">
            <div className="bg-blue-500 text-white p-4 rounded-lg shadow-md">
              <p className="font-bold text-lg">MasterCard</p>
              <p>Total Balance: $219.78</p>
              <p>**** **** **** 3783</p>
            </div>
            <div className="bg-yellow-500 text-white p-4 rounded-lg shadow-md">
              <p className="font-bold text-lg">MasterCard</p>
              <p>Total Balance: $219.78</p>
              <p>**** **** **** 3783</p>
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-lg font-semibold mb-2">Add New Bank Account</h2>
          <form className="bg-white p-4 rounded-lg shadow-md space-y-4">
            <input
              type="text"
              placeholder="Bank Name"
              className="w-full p-2 border border-gray-300 rounded"
            />
            <input
              type="text"
              placeholder="Account Name"
              className="w-full p-2 border border-gray-300 rounded"
            />
            <input
              type="text"
              placeholder="Account Number"
              className="w-full p-2 border border-gray-300 rounded"
            />
            <input
              type="text"
              placeholder="Swift Code"
              className="w-full p-2 border border-gray-300 rounded"
            />
            <input
              type="text"
              placeholder="Branch"
              className="w-full p-2 border border-gray-300 rounded"
            />
            <button
              type="submit"
              className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
            >
              Add
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CustomerDashboard;