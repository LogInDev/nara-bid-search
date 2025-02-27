// import './App.css'
import BidTable from "./components/BidTable";
import ProcurementTable from "./components/ProcurementTable";

function App() {
  return (
    <div>
      <h1>입찰 공고 시스템</h1>
      <ProcurementTable />
      <BidTable />
    </div>
  );
}


export default App
