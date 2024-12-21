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
      </div>
    </div>
  );
};

export default CustomerDashboard;