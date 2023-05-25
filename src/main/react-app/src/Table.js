import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import React, { useState, useEffect, useImperativeHandle } from 'react';
import axios from 'axios';
import moment from 'moment';
import column from './TableColumn';

const Table = React.forwardRef(({ startDate, endDate }, ref) => {
    const [data, setData] = useState([]);
    const [totalRecords, setTotalRecords] = useState(0);

    useEffect(() => {
        filterTransactions(startDate, endDate, {});
    }, [startDate, endDate]);
    
    const filterTransactions = async (startDate, endDate, isZakatInterest) => {
        try {
            let isZakatOrInterest;
            if (isZakatInterest && isZakatInterest.isZakat) {
                isZakatOrInterest = { "isZakat": true };
            } else if (isZakatInterest && isZakatInterest.isInterest) {
                isZakatOrInterest = { "isInterest": true };
            }
            const formattedStartDate = startDate ? moment(startDate).format('YYYY-MM-DDTHH:mm:ss.SSS[Z]').slice(0, 10) : null;
            const formattedEndDate = endDate ? moment(endDate).format('YYYY-MM-DDTHH:mm:ss.SSS[Z]').slice(0, 10) : null;
            const response = await axios.post('http://localhost:8080/v1/transactions/filter', {
                fromDate: formattedStartDate,
                toDate: formattedEndDate,
                ...isZakatOrInterest,
            });
            const filteredData = response.data.map(({ transactionDetailId, transactionDate, transactionRemarks, depositAmount, withdrawalAmount, balance }) => ({
                transactionDetailId,
                transactionDate,
                transactionRemarks, depositAmount, withdrawalAmount, balance
            }));
            setData(filteredData);
            setTotalRecords(filteredData.length)
        } catch (error) {
            console.error(error);
        }
    };

    useImperativeHandle(ref, () => ({
        filterTransactions,
    }));

    const paginationOptions = {
        totalSize: totalRecords,
    };

    return (
        <div>
            <BootstrapTable
                keyField="transactionDetailId"
                data={data}
                columns={column}
                pagination={paginationFactory(paginationOptions)} sortByColumn={false}
            /><p>Total Records: {totalRecords}</p>
        </div>
    );
});

export default Table;