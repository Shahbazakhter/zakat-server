import './App.css';
import React, { useState, useRef } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import Table from './Table';
import Zakat from './Zakat';
import Interest from './Interest';
import DatePickerAbstract from './DatePickerAbstract';

function App() {

  const fileInputRef = React.useRef(null); // add a ref for the file input element
  const [selectedFile, setSelectedFile] = useState(null);
  const [warningMessage, setWarningMessage] = useState([]); // added state for error message
  const [errorMessage, setErrorMessage] = useState(null); // added state for error message
  const [successMessage, setSuccessMessage] = useState(null); // added state for error message
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  const tableRef = useRef(null);
  const zakatRef = useRef(null);
  const interestRef = useRef(null);

  const handleFileInput = (e) => {
    setSelectedFile(e.target.files[0]);
  }

  const handleFilterTransactions = (e) => {
    let isZakatInterest;
    if (e && e.isZakat) {
      isZakatInterest = { "isZakat": true };
    } else if (e && e.isInterest) {
      isZakatInterest = { "isInterest": true };
    }
    tableRef.current && tableRef.current.filterTransactions(startDate, endDate, isZakatInterest);
  };

  const handleZakat = () => {
    zakatRef.current && zakatRef.current.calculateZakat(startDate, endDate);
  }

  const handleInterest = () => {
    interestRef.current && interestRef.current.calculateInterest(startDate, endDate);
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedFile) {
      setWarningMessage(["Please select the file first !"]);
      setSuccessMessage(null);
      return;
    }
    const formData = new FormData();
    formData.append('file', selectedFile);
    axios.post('http://localhost:8080/v1/statement/upload', formData)
      .then(res => {
        setSelectedFile(null);
        fileInputRef.current.value = '';
        if (res.data.errorCode === 'DUPLICATE_ENTRIES') {
          setWarningMessage(["Duplicate Entries found ! ", ...res.data.errorMessage]);
          setErrorMessage(null);
        }
        if (res.data.successMessage === 'File Uploaded Successfully') {
          setSuccessMessage(res.data.successMessage);
          setWarningMessage(null);
          tableRef.current && tableRef.current.filterTransactions();
        }
      })
      .catch(err => {
        setErrorMessage(err.response?.data?.errorMessage[0] || 'An error occurred');
        setWarningMessage(null);
        setSelectedFile(null);
        fileInputRef.current.value = '';
      });
  }

  return (
    <div className="container mt-5">
      <h1 className="text-center my-4">ZAKAT CALC</h1>

      {errorMessage && (
        <div className="alert alert-danger" role="alert">
          {errorMessage}
        </div>
      )}

      {warningMessage && warningMessage.length > 0 && (
        <div className="alert alert-warning" role="alert">
          {warningMessage.map((error, index) => (
            <div key={index}>{error}</div>
          ))}

        </div>
      )}

      {successMessage && (
        <div className="alert alert-success" role="alert">
          {successMessage}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="file">Choose Statements to Upload</label> <pre />
          <input type="file" className="form-control-file" id="file" onChange={handleFileInput} ref={fileInputRef} />
          <button type="submit" className="btn btn-primary">Upload</button> <pre />
        </div>
      </form>
      <DatePickerAbstract startDate={startDate}
        setStartDate={setStartDate}
        handleZakat={handleZakat}
        handleInterest={handleInterest}
        endDate={endDate}
        setEndDate={setEndDate}
        handleFilterTransactions={handleFilterTransactions} />

      <div style={{ display: "inline-block" }}>
        <Zakat successMessage={successMessage} setSuccessMessage={setSuccessMessage}
          startDate={startDate} ref={zakatRef} setErrorMessage={setErrorMessage}
          endDate={endDate}
          setStartDate={setStartDate}
          setEndDate={setEndDate}
          handleFilterTransactions={handleFilterTransactions} />
      </div>
      <div style={{ display: "inline-block" }}>
        <Interest successMessage={successMessage} setSuccessMessage={setSuccessMessage}
          startDate={startDate} ref={interestRef} setErrorMessage={setErrorMessage}
          endDate={endDate}
          setStartDate={setStartDate}
          setEndDate={setEndDate}
          handleFilterTransactions={handleFilterTransactions} />
      </div>
      <pre />
      <Table ref={tableRef} startDate={startDate}
        endDate={endDate} />
    </div>
  );
}

export default App;
