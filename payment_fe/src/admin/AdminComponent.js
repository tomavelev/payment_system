import { Component } from "react";
import { Container, Tab, Tabs } from "react-bootstrap";
import TransactionListComponent from "../mertchant/TransactionListComponent";
import UserListComponent from "./UserListComponent";

class AdminComponent extends Component {



    render() {
        return (
            <Container>
                <h1>Admin of Payment System</h1>
                <Tabs
                    defaultActiveKey="users"
                    id="admin_tab"
                    className="mb-3"
                >
                    <Tab eventKey="users" title="Users">
                        <UserListComponent />
                    </Tab>
                    <Tab eventKey="transactions" title="Transactions">
                        <TransactionListComponent />
                    </Tab>
                </Tabs>
            </Container>
        )
    }
}

export default AdminComponent