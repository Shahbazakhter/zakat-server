import logo from './logo.svg';
import './App.css';
import React, { useState, useRef } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import Table from './Table';

function App() {
  const fileInputRef = React.useRef(null); // add a ref for the file input element
  const [selectedFile, setSelectedFile] = useState(null);
  const [errorMessage, setErrorMessage] = useState([]); // added state for error message
  const [successMessage, setSuccessMessage] = useState(null); // added state for error message
  const tableRef = useRef(null);

  const handleFileInput = (e) => {
    setSelectedFile(e.target.files[0]);
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append('file', selectedFile);
    axios.post('http://localhost:8080/v1/statement/upload', formData)
      .then(res => {
        setSelectedFile(null);
        fileInputRef.current.value = '';
        console.log(res.data);
        if (res.data.errorCode == 'DUPLICATE_ENTRIES') {
          setErrorMessage(["Duplicate Entries found ! ", ...res.data.errorMessage]);
          console.table(errorMessage);
        }
        if (res.data.successMessage == 'File Uploaded Successfully') {
          setSuccessMessage(res.data.successMessage);
          setErrorMessage(null);
          tableRef.current.filterTransactions();
        }
      })
      .catch(err => {
        if (selectedFile == null) {
          setErrorMessage(["Please select the file first !"]);
          setSuccessMessage(null);
          return;
        }
        setSelectedFile(null);
        fileInputRef.current.value = '';
        console.log("ERROR OCCORRED");
        console.error(err);
      });
  }

  const calculateZakat = async () => {
    try {
      const response = await axios.get('https://jsonplaceholder.typicode.com/todos/1');
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="container mt-5">
      <h1 className="text-center my-4">Zakat Calculator</h1>
      {successMessage && (
        <div className="alert alert-success" role="alert">
          {successMessage}
        </div>
      )}
      {errorMessage && errorMessage.length > 0 && (
        <div className="alert alert-warning" role="alert">
          {errorMessage.map((error, index) => (
            <div key={index}>{error}</div>
          ))}

        </div>
      )}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="file">Choose Statements to Upload</label> <pre />
          <input type="file" className="form-control-file" id="file" onChange={handleFileInput} ref={fileInputRef} /> <pre />
        </div>
        <button type="submit" className="btn btn-primary">Upload</button> <pre />
      </form>
      <button onClick={calculateZakat} className="btn btn-primary">Calculate Zakat</button>
      <Table ref={tableRef} />
    </div>
  );
}

export default App;
