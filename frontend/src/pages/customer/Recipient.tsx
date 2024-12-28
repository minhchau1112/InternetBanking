import { Edit, Delete } from '@mui/icons-material';


const Recipient = () => {
    return (
        <div className="min-h-screen w-full bg-gray-100 p-6">
            <div className="flex-col justify-between">
                <h1 className="text-2xl font-bold mb-2">Customer Recipients</h1>
                <h2 className="text-lg font-light mb-10">Manage your recipients</h2>
                <div className="min-h-full w-3/5 rounded-2xl bg-white p-6">
                    <h3 className="text-3xl font-bold mb-4">Recipients</h3>
                    <div className="w-full p-[2%] border border-gray-200 rounded-2xl">
                        <table className="w-full text-lg text-left border-collapse">
                            <thead>
                            <tr>
                                <th className="py-[2%] px-[3%] w-[30%] text-blue-600">Alias Name</th>
                                <th className="py-[2%] px-[3%] w-[30%] text-blue-600">Account Number</th>
                                <th className="py-[2%] px-[3%] w-[20%] text-blue-600">Bank Code</th>
                                <th className="py-[2%] px-[3%] w-[20%]"></th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td className="py-[2%] px-[3%]">Alias Name</td>
                                <td className="py-[2%] px-[3%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%] px-[3%]">HBS</td>
                                <td className="py-[2%] px-[3%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr>
                                <td className="py-[2%] px-[3%]">Fadhil Aksara</td>
                                <td className="py-[2%] px-[3%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%] px-[3%]">HBS</td>
                                <td className="py-[2%] px-[3%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr>
                                <td className="py-[2%] px-[3%]">Fadhil Rubian</td>
                                <td className="py-[2%] px-[3%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%] px-[3%]">HBS</td>
                                <td className="py-[2%] px-[3%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr>
                                <td className="py-[2%] px-[3%]">Fadhil Gimari</td>
                                <td className="py-[2%] px-[3%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%] px-[3%]">HBS</td>
                                <td className="py-[2%] px-[3%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr>
                                <td className="py-[2%] px-[3%]">Fadhil Muhammad</td>
                                <td className="py-[2%] px-[3%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%] px-[3%]">HBS</td>
                                <td className="py-[2%] px-[3%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr>
                                <td className="py-[2%] px-[3%]">Fadhil Sausu</td>
                                <td className="py-[2%] px-[3%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%] px-[3%]">HBS</td>
                                <td className="py-[2%] px-[3%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr>
                                <td className="py-[2%] px-[3%]">Fadhil Sausu</td>
                                <td className="py-[2%] px-[3%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%] px-[3%]">HBS</td>
                                <td className="py-[2%] px-[3%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr>
                                <td className="py-[2%] px-[3%]">Fadhil Sausu</td>
                                <td className="py-[2%] px-[3%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%] px-[3%]">HBS</td>
                                <td className="py-[2%] px-[3%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr>
                                <td className="py-[2%] px-[3%]">Fadhil Sausu</td>
                                <td className="py-[2%] px-[3%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%] px-[3%]">HBS</td>
                                <td className="py-[2%] px-[3%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <div className="flex justify-center mt-[2%] space-x-[1%]">
                            <button className="py-[1%] px-[2%] bg-yellow-400 rounded-full">1</button>
                            <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">2</button>
                            <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">3</button>
                            <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">4</button>
                            <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">5</button>
                            <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">&gt;</button>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    )
}

export default Recipient;