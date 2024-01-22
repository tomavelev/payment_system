import { BrowserRouter, Route, Routes } from 'react-router-dom';
import './App.css';
import LoginComponent from './login/LoginComponent';
import UserComponent from './mertchant/UserComponent';
import AdminComponent from './admin/AdminComponent';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' Component={LoginComponent} />
        <Route path='/admin' Component={AdminComponent} />
        <Route path='/user' Component={UserComponent} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
