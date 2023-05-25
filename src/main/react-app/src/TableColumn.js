
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';

const column =
    [
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

export default column;