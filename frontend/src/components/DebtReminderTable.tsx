import React, { useEffect, useState } from 'react';
import {
	Box,
	Button,
	Chip,
} from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { DeleteOutline, CurrencyExchangeOutlined } from '@mui/icons-material';
import { format } from 'date-fns';
import { fetchDebtRemindersForCreator, fetchDebtRemindersForDebtor } from '../services/debtReminderService'; 

function capitalizeFirstLetter(str: string) {
	if (!str) return '';
	return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

interface DataTableProps {
	status?: string;
	type?: string;
}

const DebtReminderTable: React.FC<DataTableProps> = ({ status = 'PENDING', type = 'Creator' }) => {
	const accountId = localStorage.getItem('accountId') || '3';
	const id = parseInt(accountId, 10);

	const [rows, setRows] = useState<any[]>([]);
	const [page, setPage] = useState(0);
	const [pageSize, setPageSize] = useState(10);
	const [rowCount, setRowCount] = useState(0); 
	const [loading, setLoading] = useState(false);

	const columns: GridColDef[] = [
		{ 
			field: 'id', 
			width: 70,
			headerAlign: 'right',
			headerClassName: 'theme--header',
			renderHeader: () => (
				<strong>
					#
				</strong>
			),
			renderCell: (params) => (
				<Box 
					sx={{ 
						display: 'flex', 
						alignItems: 'center', 
						justifyContent: 'flex-end',
						height: '100%', 
						width: '100%',
					}}
				>
					{params.value}
				</Box>
			),
		},
			{ 
			field: 'debtAccountNumber', 
			width: 150 ,
			headerAlign: 'right',
			headerClassName: 'theme--header',
			renderHeader: () => (
				<strong>
					Account Number
				</strong>
			),
			renderCell: (params) => (
				<Box 
					sx={{ 
						display: 'flex', 
						alignItems: 'center', 
						justifyContent: 'flex-end',
						height: '100%', 
						width: '100%',
					}}
				>
					{params.value}
				</Box>
			),
		},
			{
			field: 'debtName',
			width: 220,
			headerAlign: 'right',
			headerClassName: 'theme--header',
			renderHeader: () => (
				<strong>
					Name
				</strong>
			),
			renderCell: (params) => (
				<Box 
					sx={{ 
					display: 'flex', 
					alignItems: 'center', 
					justifyContent: 'flex-end',
					height: '100%', 
					width: '100%',
					paddingLeft: 1
					}}
				>
					<strong>{params.value}</strong>
				</Box>
			),
		},
		{ 
			field: 'amount', 
			width: 160,
			headerAlign: 'right',
			headerClassName: 'theme--header',
			renderHeader: () => (
				<strong>
					Amount
				</strong>
			),
			renderCell: (params) => (
				<Box 
					sx={{ 
						display: 'flex', 
						alignItems: 'center', 
						justifyContent: 'flex-end',
						height: '100%', 
						width: '100%',
					}}
				>
					{params.value}
				</Box>
			),
		},
		{ 
			field: 'message', 
			width: 300,
			headerAlign: 'center',
			headerClassName: 'theme--header',
			renderHeader: () => (
				<strong>
					Message
				</strong>
			),
		},
		{
			field: 'status',
			width: 150,
			headerAlign: 'center',
			headerClassName: 'theme--header',
			renderHeader: () => (
				<strong>
					Status
				</strong>
			),
			renderCell: (params) => {
				const getChipStyles = (status: string) => {
					switch (status) {
					case 'Pending':
						return {
						backgroundColor: '#F0F9FF',
						color: '#026AA2',
						borderColor: '#B9E6FE',
						};
					case 'Paid':
						return {
						backgroundColor: '#ECFDF3',
						color: '#079455',
						borderColor: '#ABEFC6',
						};
					case 'Cancelled':
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
	
				const styles = getChipStyles(params.value);
	
				return (
					<Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
						<Chip
							label={params.value}
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
			},
		},
		{ 
			field: 'createdTime', 
			width: 180,
			headerAlign: 'center',
			headerClassName: 'theme--header',
			renderHeader: () => (
				<strong>
					Created Time
				</strong>
			),
			renderCell: (params) => {
				const formattedDate = format(new Date(params.value), 'yyyy-MM-dd HH:mm:ss');
				return (
					<Box 
						sx={{ 
							display: 'flex', 
							alignItems: 'center', 
							justifyContent: 'center',
							height: '100%', 
							width: '100%',
						}}
					>
						{formattedDate}
					</Box>
				)
			},
		},
		{
			field: 'actions',
			width: 200,
			headerAlign: 'center',
			headerClassName: 'theme--header',
			renderHeader: () => (
				<strong>
					Actions
				</strong>
			),
			renderCell: () => (
				<Box display="flex" justifyContent={'center'} gap={1} sx={{ display: 'flex', alignItems: 'center', height: '100%', width: '100%' }}>
					{type === 'Creator' ? (
						<Button variant="contained" color="error" startIcon={<DeleteOutline />}>
							Cancel
						</Button>
					) : (
						<>
							<Button variant="contained" color="success" startIcon={<CurrencyExchangeOutlined />}>
								Pay
							</Button>
							<Button variant="contained" color="error" startIcon={<DeleteOutline />}>
								Cancel
							</Button>
						</>
					)}
				</Box>
			),
		},
	  
  	];

	const fetchRows = async (type: string, id: number, status: string, page: number, pageSize: number) => {
		setLoading(true);
		try {
			let data = null;

			if (type === 'Creator') {
				data = await fetchDebtRemindersForCreator(id, status, page, pageSize);
			} else {
				data = await fetchDebtRemindersForDebtor(id, status, page, pageSize);
			}

			setRows(
				data.content.map((item: any, index: number) => ({
					id: index + 1 + page * pageSize,
					debtAccountNumber: item.debt_account_number,
					debtName: item.debt_name,
					email: item.email,
					amount: item.amount,
					message: item.message,
					status: capitalizeFirstLetter(item.status),
					createdTime: item.created_time,
				}))
			);
			setRowCount(data.totalElements); 
		} catch (error) {
			console.error('Error fetching rows:', error);
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		fetchRows(type, id, status, page, pageSize);
	}, [type, id, status, page, pageSize]);

	return (
		<Box sx=
				{{ 
					height: 630, 
					width: '100%', 
					'& .theme--header': {
						backgroundColor: '#F9FAFB',
					},
					'& .MuiDataGrid-columnHeaderCheckbox': {
						backgroundColor: '#F9FAFB', 
					},
				}}
		>
			<DataGrid
				rows={rows}
				columns={columns}
				paginationMode="server" 
				rowCount={rowCount} 
				loading={loading} 
				initialState={{
					pagination: {
					paginationModel: { pageSize: pageSize, page: page },
					},
				}}
				onPaginationModelChange={(model) => {
					setPage(model.page); 
					setPageSize(model.pageSize); 
				}}
				pageSizeOptions={[5, 10, 20]} 
				checkboxSelection
			/>
		</Box>
	);
};

export default DebtReminderTable;