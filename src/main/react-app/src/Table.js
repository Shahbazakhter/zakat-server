import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import React, { useState, useEffect, useImperativeHandle } from 'react';
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';

const Table = React.forwardRef((props, ref) => {
    const [data, setData] = useState([]);
    const [totalRecords, setTotalRecords] = useState(0);
    const [currentPage, setCurrentPage] = useState(1);
    useEffect(() => {
        filterTransactions();
    }, []);

    const filterTransactions = async () => {
        try {
            const response = await axios.post('http://localhost:8080/v1/transactions/filter', {
                fromDate: '2021-01-05',
                toDate: '2023-05-05',
                remarksData: '',
                sortByColumn: '',
                sortOrder: '',
            });
            console.log(response.data);
            const filteredData = response.data.map(({ transactionDetailId, transactionDate, transactionRemarks, depositAmount, withdrawalAmount, balance }) => ({
                transactionDetailId,
                transactionDate,
                transactionRemarks, depositAmount, withdrawalAmount, balance
            }));
            setData(filteredData); // Store the fetched data
            setTotalRecords(filteredData.length)
        } catch (error) {
            console.error(error);
        }
    };
    const columns = [
        { dataField: 'transactionDetailId', text: 'S.No', sort: true },
        {
            dataField: 'transactionDate', text: 'Transaction Date', sort: true,
            sortCaret: (order, column) => {
                if (!order) return <FontAwesomeIcon icon={faSort} />;
                if (order === 'asc') return <FontAwesomeIcon icon={faSortUp} />;
                if (order === 'desc') return <FontAwesomeIcon icon={faSortDown} />;
                return null;
            }
        },
        { dataField: 'transactionRemarks', text: 'Remarks' },
        { dataField: 'depositAmount', text: 'Deposit Amount', sort: true },
        { dataField: 'withdrawalAmount', text: 'Withdrawal Amount', sort: true },
        {
            dataField: 'balance', text: 'Balance', sort: true,
            sortCaret: (order, column) => {
                if (!order) return <FontAwesomeIcon icon={faSort} />;
                if (order === 'asc') return <FontAwesomeIcon icon={faSortUp} />;
                if (order === 'desc') return <FontAwesomeIcon icon={faSortDown} />;
                return null;
            }
        },
    ];

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    useImperativeHandle(ref, () => ({
        filterTransactions,
    }));

    const paginationOptions = {
        sizePerPage: 15, totalSize: totalRecords,
        totalSize: data.length,
        hideSizePerPage: true,
        onPageChange: handlePageChange,
    };

    return (
        <div>
            <BootstrapTable
                keyField="transactionDetailId"
                data={data}
                columns={columns}
                pagination={paginationFactory(paginationOptions)} sortByColumn={false}
            /><p>Total Records: {totalRecords}</p>
        </div>
    );
});

export default Table;