import { useForm, SubmitHandler } from 'react-hook-form';

const AccountCreation = () => {
    const { register, handleSubmit, formState: { errors } } = useForm<FormData>();

    type FormData = {
        username: string;
        email: string;
        name: string;
        phone: string;
    };

    const onSubmit: SubmitHandler<FormData> = (data) => {
        console.log(data);
        // Perform account creation logic here
        try {
            fetch('http://localhost:8888/api/accounts', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: data.username,
                    name: data.name,
                    email: data.email,
                    phone: data.phone,
                    role: 'customer'
                }),
            })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert(data.error);
                } else {
                    alert('Account created successfully');
                    console.log('Success:', data);
                }
            });
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <div className="w-full h-screen flex items-center justify-center bg-gray-100 min-w-[600px]">
            <div className="w-full bg-white p-8 rounded-lg shadow-md">
                <h1 className="text-3xl font-bold mb-6 text-gray-800 text-center">Create Customer Account</h1>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-6 w-full">
                    <div>
                        <label className="block text-gray-700 text-lg mb-2">Username</label>
                        <div className="flex items-center">
                            <input
                                {...register('username', { required: "Username is required" })}
                                className="border border-gray-300 rounded-lg px-4 py-3 w-full text-lg"
                            />
                            <div className="text-red-500 ml-4 text-sm">
                                {errors.username?.message && String(errors.username.message)}
                            </div>
                        </div>
                    </div>

                    <div>
                        <label className="block text-gray-700 text-lg mb-2">Email</label>
                        <div className="flex items-center">
                            <input
                                {...register('email', { 
                                    required: "Email is required", 
                                    pattern: { 
                                        value: /^\S+@\S+$/i, 
                                        message: "Invalid email address" 
                                    } 
                                })}
                                className="border border-gray-300 rounded-lg px-4 py-3 w-full text-lg"
                            />
                            <div className="text-red-500 ml-4 text-sm">
                                {errors.email?.message && String(errors.email.message)}
                            </div>
                        </div>
                    </div>

                    <div>
                        <label className="block text-gray-700 text-lg mb-2">Name</label>
                        <div className="flex items-center">
                            <input
                                {...register('name', { required: "Name is required" })}
                                className="border border-gray-300 rounded-lg px-4 py-3 w-full text-lg"
                            />
                            <div className="text-red-500 ml-4 text-sm">
                                {errors.name?.message && String(errors.name.message)}
                            </div>
                        </div>
                    </div>

                    <div>
                        <label className="block text-gray-700 text-lg mb-2">Phone</label>
                        <div className="flex items-center">
                            <input
                                {...register('phone', { 
                                    required: "Phone is required", 
                                    pattern: { 
                                        value: /^[0-9]{10}$/, 
                                        message: "Invalid phone number, must be 10 digits" 
                                    }
                                })}
                                className="border border-gray-300 rounded-lg px-4 py-3 w-full text-lg"
                            />
                            <div className="text-red-500 ml-4 text-sm">
                                {errors.phone?.message && String(errors.phone.message)}
                            </div>
                        </div>
                    </div>

                    <div className="text-center">
                        <button
                            type="submit"
                            className="bg-blue-600 hover:bg-blue-700 text-white font-bold px-6 py-3 rounded-lg text-lg"
                        >
                            Create Account
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AccountCreation;
