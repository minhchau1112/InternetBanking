import React, { useEffect } from "react";
import {
	Box,
	Button,
	Chip,
} from '@mui/material';
import { useDispatch, useSelector } from "react-redux";
import { RootState, AppDispatch } from "../redux/store";
import {
  setRows,
  setPage,
  setPageSize,
  setRowCount,
  setLoading,
  setSelectedDebtId,
  setOpenDialog,
  setOpenOtpDialog,
  setOtpError,
} from "../redux/slices/debtReminderTableSlice";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import CancelDialog from "./CancelDialog";
import OtpDialog from "./OtpDialog";
import { fetchDebtRemindersForCreator, fetchDebtRemindersForDebtor, cancelDebtReminder, payDebtReminder } from "../services/debtReminderService";
import { fetchCustomer } from "../services/customerService";
import { format } from 'date-fns';
import { DeleteOutline, CurrencyExchangeOutlined } from '@mui/icons-material';
import { initiateTransfer } from "../services/internalTransferService";
import { useSnackbar } from "notistack";

const DebtReminderTable = ({ status = "PENDING", type = "Creator" }) => {
  const dispatch = useDispatch<AppDispatch>();
  const {
    rows,
    page,
    pageSize,
    rowCount,
    loading,
    selectedDebtId,
    openDialog,
    openOtpDialog,
    otpError,
  } = useSelector((state: RootState) => state.debtReminderTable);

  const { enqueueSnackbar } = useSnackbar();
  const accountId = localStorage.getItem("accountId") || "3";
  const accessToken = localStorage.getItem("access_token") || "";
  const id = parseInt(accountId, 10);

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
		field: 'accountNumber', 
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
		field: 'accountName',
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
		width: 360,
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
		renderCell: (params) => (
			<Box display="flex" justifyContent={'center'} gap={1} sx={{ display: 'flex', alignItems: 'center', height: '100%', width: '100%' }}>
				{type === 'Creator' ? (
					<Button variant="contained" 
							color="error" 
							startIcon={<DeleteOutline />}
							onClick={() => handleCancelClick(params.row.debtReminderId)}>
						Cancel
					</Button>
				) : (
					<>
						<Button variant="contained" 
								color="success" 
								startIcon={<CurrencyExchangeOutlined />}
								onClick={() => handlePayClick(params.row.creatorAccountId, params.row.amount, params.row.message, params.row.debtReminderId)}>
							Pay
						</Button>
						<Button variant="contained" 
								color="error" 
								startIcon={<DeleteOutline />}
								onClick={() => handleCancelClick(params.row.debtReminderId)}>
							Cancel
						</Button>
					</>
				)}
			</Box>
		),
	},
  
  ];

	function capitalizeFirstLetter(str: string) {
		if (!str) return '';
		return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
	}

	const fetchRows = async (type: string, id: number, status: string, page: number, pageSize: number) => {
		dispatch(setLoading(true));
		try {
			let data = null;

			if (type === 'Creator') {
				let response = await fetchDebtRemindersForCreator(id, status, page, pageSize, accessToken);
				data = response;

				console.log('data: ', data);

				if (data && data.content) { 
					dispatch(setRows(
						data.content.map((item: any, index: number) => ({
							id: index + 1 + page * pageSize,
							accountNumber: item.debt_account_number,
							accountName: item.debt_name,
							amount: item.amount,
							message: item.message,
							status: capitalizeFirstLetter(item.status),
							createdTime: item.created_time,
							debtReminderId: item.debt_reminder_id,
						}))
					));
					dispatch(setRowCount(data.totalElements)); 
				}
			} else {
				let response = await fetchDebtRemindersForDebtor(id, status, page, pageSize, accessToken);
				data = response;

				console.log('data: ', data);
				
				if (data && data.content) { 
					dispatch(setRows(
						data.content.map((item: any, index: number) => ({
							id: index + 1 + page * pageSize,
							creatorAccountId: item.creator_account_id,
							accountNumber: item.creator_account_number,
							accountName: item.creator_name,
							amount: item.amount,
							message: item.message,
							status: capitalizeFirstLetter(item.status),
							createdTime: item.created_time,
							debtReminderId: item.debt_reminder_id,
						}))
					));
					dispatch(setRowCount(data.totalElements)); 
				}
			}
		} catch (error) {
			console.error('Error fetching rows:', error);
		} finally {
			dispatch(setLoading(false));
		}
	};

	useEffect(() => {
		fetchRows(type, id, status, page, pageSize);
	}, [type, id, status, page, pageSize]);

  const handleCancelClick = (debtReminderId: number) => {
    dispatch(setSelectedDebtId(debtReminderId));
    dispatch(setOpenDialog(true));
  };

  const handleConfirmCancel = async (reason: string) => {
    if (selectedDebtId && reason.trim()) {
		dispatch(setLoading(true));
		try {
			const response = await cancelDebtReminder(selectedDebtId, id, reason.trim(), accessToken);
			if (response.status == 200) {
				enqueueSnackbar(response.message, { variant: 'success', autoHideDuration: 1500 });
				console.log("fetch");
				fetchRows(type, id, status, page, pageSize);
			} else {
				enqueueSnackbar(response.message, { variant: 'error', autoHideDuration: 1500 });
				console.error(response.message);
			}
		} catch (error) {
			console.error('Error cancelling debt reminder:', error);
			enqueueSnackbar('Failed to cancel debt reminder!', { variant: 'error', autoHideDuration: 1500 });
		} finally {
			dispatch(setLoading(false));
			dispatch(setOpenDialog(false));
		}
    }
  };

  const handlePayClick = async (destinationAccountId: number, amount: number, message: string, debtReminderId: number) => {
	let otpString;
	dispatch(setSelectedDebtId(debtReminderId));
	const internalTransferRequest = {
		sourceAccountId: id,
		destinationAccountId: destinationAccountId,
		amount: amount,
		message: message,
		feePayer: "SENDER",
	};

	try {
		const response = await initiateTransfer(internalTransferRequest, accessToken);
		console.log("handlePayClick", response);

		if (response.status == 200) {
			otpString = response.data.otp;
			console.log("setOpenOtpDialog = true");
			dispatch(setOpenOtpDialog(true)); 
		  }
		} catch (error) {
		  console.error('Error generate otp:', error);
		}
	};
  	const handleOtpSubmit = async (otp: string) => {
		try {
			const customer = await fetchCustomer(parseInt(accountId), accessToken);
			
			console.log("customer: ", customer);
			const email = customer.data.email;

			const response = await payDebtReminder(selectedDebtId, otp, email, accessToken);
			console.log("handleOtpSubmit: ", response);

			if (response.status === 200) {
				fetchRows(type, id, status, page, pageSize);
				dispatch(setOpenOtpDialog(false));
				enqueueSnackbar('Payment successful!', { variant: 'success', autoHideDuration: 3000 });
			} else {
				dispatch(setOtpError(response.message));
			}
			} catch (error) {
			dispatch(setOtpError("Failed to process OTP."));
			console.error('Error processing OTP:', error);
		}
	};

  return (
    <div>
      <DataGrid
        rows={rows}
        columns={columns}
        rowCount={rowCount}
        loading={loading}
		paginationMode="server"
		initialState={{
			pagination: {
				paginationModel: { pageSize: pageSize, page: page },
			},
		}}
		onPaginationModelChange={(model) => {
			dispatch(setPage(model.page)); 
			dispatch(setPageSize(model.pageSize)); 
		}}
      />
      <CancelDialog open={openDialog} onClose={() => dispatch(setOpenDialog(false))} onConfirm={handleConfirmCancel} />
      <OtpDialog
        open={openOtpDialog}
        onClose={() => dispatch(setOpenOtpDialog(false))}
        onConfirm={handleOtpSubmit}
        errorMessage={otpError}
      />
    </div>
  );
};

export default DebtReminderTable;
