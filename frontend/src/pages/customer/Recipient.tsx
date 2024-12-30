import { Edit, Delete, NavigateBefore, NavigateNext } from '@mui/icons-material';


const Recipient = () => {
    return (
        <div className="w-[calc(100vw-256px)] max-w-full h-screen flex items-center justify-center p-8 bg-gray-100 ">
            <div className="w-full h-full bg-white p-8 rounded-lg shadow-md">
                <h1 className="text-3xl font-bold mb-6 text-gray-800 text-left">Manage Customer Recipients</h1>
                <div className="w-full grid grid-cols-3">
                    <div className="w-full h-full flex items-center justify-center col-span-2">
                        <div className="w-full p-[2%] border border-gray-200 rounded-3xl">
                        <table className="w-full text-lg text-left p-[2%] border-collapse">
                            <thead>
                            <tr className="border-b">
                                <th className="py-[2%] w-[40%] font-bold">Alias Name</th>
                                <th className="py-[2%] w-[30%] font-bold">Account Number</th>
                                <th className="py-[2%] w-[20%] font-bold">Bank Code</th>
                                <th className="py-[2%] w-[10%]"></th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr className="border-b">
                                <td className="py-[2%]">Alias Name</td>
                                <td className="py-[2%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%]">HBS</td>
                                <td className="py-[2%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr className="border-b">
                                <td className="py-[2%]">Fadhil Aksara</td>
                                <td className="py-[2%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%]">HBS</td>
                                <td className="py-[2%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr className="border-b">
                                <td className="py-[2%]">Fadhil Rubian</td>
                                <td className="py-[2%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%]">HBS</td>
                                <td className="py-[2%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr className="border-b">
                                <td className="py-[2%]">Fadhil Gimari</td>
                                <td className="py-[2%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%]">HBS</td>
                                <td className="py-[2%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr className="border-b">
                                <td className="py-[2%]">Fadhil Muhammad</td>
                                <td className="py-[2%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%]">HBS</td>
                                <td className="py-[2%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr className="border-b" >
                                <td className="py-[2%]">Fadhil Sausu</td>
                                <td className="py-[2%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%]">HBS</td>
                                <td className="py-[2%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr className="border-b">
                                <td className="py-[2%]">Fadhil Sausu</td>
                                <td className="py-[2%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%]">HBS</td>
                                <td className="py-[2%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr className="border-b">
                                <td className="py-[2%]">Fadhil Sausu</td>
                                <td className="py-[2%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%]">HBS</td>
                                <td className="py-[2%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            <tr className="border-b">
                                <td className="py-[2%]">Fadhil Sausu</td>
                                <td className="py-[2%] text-gray-400">1xxxxxxxxxx0</td>
                                <td className="py-[2%]">HBS</td>
                                <td className="py-[2%] flex">
                                    <button className="bg-transparent p-2"><Edit/></button>
                                    <button className="bg-transparent p-2"><Delete/></button>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                            <div className="flex justify-center items-center mt-[2%] space-x-[1.5%]">
                                <button className="py-1 px-1 bg-black rounded-full"><NavigateBefore style={{ color: "white", fontSize: 30 }}/></button>
                                <p className="text-lg">Page 1/10</p>
                                <button className="py-1 px-1 bg-black rounded-full"><NavigateNext style={{ color: "white", fontSize: 30 }}/></button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        // <div className="min-h-screen w-full bg-gray-100 p-6">
        //     <div className="flex-col justify-between">
        //         <div className="min-h-full w-3/5 rounded-2xl bg-white p-6">
        //             <h3 className="text-3xl font-bold mb-4">Recipients</h3>
        //             <div className="w-full p-[2%] border border-gray-200 rounded-2xl">

        //                 <div className="flex justify-center mt-[2%] space-x-[1%]">
        //                     <button className="py-[1%] px-[2%] bg-yellow-400 rounded-full">1</button>
        //                     <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">2</button>
        //                     <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">3</button>
        //                     <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">4</button>
        //                     <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">5</button>
        //                     <button className="py-[1%] px-[2%] bg-gray-200 rounded-full">&gt;</button>
        //                 </div>
        //             </div>
        //
        //         </div>
        //     </div>
        // </div>
    )
}

export default Recipient;