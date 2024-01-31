import axios from "axios";
import './TransactionListComponent.css'
import { Component } from "react";
import { Button, Card, CardBody } from "react-bootstrap";
import { TRANSACTIONS_PAGE_SIZE, host } from "..";

class TransactionListComponent extends Component {

  constructor(props) {
    super(props);
    this.state = {
      list: [],
      offset: 0,
      count: '',
      loading: false
    };

    this.firstPage = this.firstPage.bind(this);
    this.nextPage = this.nextPage.bind(this);
    this.previousPage = this.previousPage.bind(this);
    this.lastPage = this.lastPage.bind(this);
  }


  componentDidMount() {
    this._isMounted = true;
    this.load();
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  firstPage() {
    this.setState({
      offset: 0
    }, () => this.load())
  }
  previousPage() {

    this.setState({
      offset: this.state.offset - TRANSACTIONS_PAGE_SIZE
    }, () => this.load())
  }
  nextPage() {
    this.setState({
      offset: this.state.offset + TRANSACTIONS_PAGE_SIZE
    }, () => this.load())

  }
  lastPage() {
    this.setState({
      offset: this.state.count - TRANSACTIONS_PAGE_SIZE
    }, () => this.load())
  }

  load() {
    axios.get(host + '/user/transactions?offset=' + this.state.offset + "&limit=" + TRANSACTIONS_PAGE_SIZE,
      { headers: { 'Authorization': 'Bearer ' + localStorage.getItem("token") } })
      .then((response) => {
        if (this._isMounted)
          this.setState({
            list: response.data.list,
            count: response.data.count
          })
      })
      .catch(function (error) {
        //TODO show error
        console.log(error);
        if (error.response != null && error.response.status === 403) {
          window.location = '/';
        }
      });
  }
  render() {
    return (
      <div className="transactions">

        {this.state.list.map(transaction =>
          <Card key={transaction.id} className="transaction">
            <CardBody>
              <div>id: {transaction.id}</div>
              <div>createdAt: {transaction.createdAt}</div>
              <div>updatedAt: {transaction.updatedAt}</div>
              <div>customerEmail: {transaction.customerEmail}</div>
              <div>customerPhone: {transaction.customerPhone}</div>
              <div>uuid: {transaction.uuid}</div>
              <div>amount: {transaction.amount}</div>
              <div>referenceId: {transaction.referenceId}</div>
              <div>status: {transaction.status}</div>
            </CardBody></Card>
        )}

        {this.state.offset > 0 ? (
          <span>
            <Button onClick={this.firstPage}>
              First Page
            </Button>
            &nbsp;
            <Button onClick={this.previousPage}>
              Previous Page
            </Button>
          </span>
        ) : (
          ""
        )}
        &nbsp;
        {this.state.count > this.state.offset + TRANSACTIONS_PAGE_SIZE ? (
          <span>
            <Button onClick={this.nextPage}>
              Next Page
            </Button>
            &nbsp;
            <Button onClick={this.lastPage}>
              Last Page
            </Button>
          </span>
        ) : (
          ""
        )}

        <div>{this.state.offset + 1} to {this.state.offset + this.state.list.length} Total Transactions {this.state.count}</div>
      </div>
    )
  }
}

export default TransactionListComponent