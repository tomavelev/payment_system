import { Component } from "react";
import { Container } from "react-bootstrap";
import TransactionListComponent from "./TransactionListComponent";
import LogoutButton from "../login/LogoutButton";

class UserComponent extends Component {

    render() {
        return (
            <Container>
                <h1>Transactions</h1>
                <TransactionListComponent />
                <LogoutButton />
            </Container>
        )
    }
}

export default UserComponent