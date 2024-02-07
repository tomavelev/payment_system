import axios from "axios";
import { Component } from "react";
import { Button, Card, CardBody, ProgressBar } from "react-bootstrap";
import { TRANSACTIONS_PAGE_SIZE, host } from "..";
import AddEditUserComponent from "./AddEditUserComponent";
import ImportUsersComponent from "./ImportUsersComponent";
import './UserListComponent.css';

class UserListComponent extends Component {

  constructor(props) {
    super(props);
    this.state = {
      list: [],
      offset: 0,
      count: '',
      loading: false,
      item: null,
      error: null,
      csvFile: null
    };
  }


  componentDidMount() {
    this._isMounted = true;
    this.load();
  }

  componentWillUnmount() {
    this._isMounted = false;
  }
  addUser(isMerchant) {
    this.setState({
      item: {
        password: '',
        merchant: isMerchant ? {
          active: true
        } : null
      }
    })
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

  delete(id) {
    var doDelete = window.confirm("Confirm Delete");
    if (doDelete) {
      axios.delete(host + '/admin/user',
        {
          headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("token")
          }, data: {
            id: id
          }
        })
        .then((response) => {
          if (this._isMounted)
            this.load();
        })
        .catch(function (error) {
          //TODO show error
          console.log(error);
          if (error.response != null && error.response.status === 403) {
            window.location = '/';
          }
        });
    }
  }

  edit(user) {
    let clone = JSON.parse(JSON.stringify(user));

    this.setState({
      item: clone
    })
  }

  handleClose() {
    this.setState({
      item: null
    })
  }

  load() {
    this.setState({ loading: true })
    axios.get(host + '/admin/user?offset=' + this.state.offset + "&limit=" + TRANSACTIONS_PAGE_SIZE,
      {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem("token")
        }
      })
      .then((response) => {
        if (this._isMounted)
          this.setState({
            loading: false,
            list: response.data.list,
            count: response.data.count,
            item: null
          })
      })
      .catch((error) => {
        //TODO show error

        this.setState({ loading: false })
        console.log(error);
        if (error.response != null && error.response.status === 403) {
          window.location = '/';
        }
      });
  }
  render() {
    return (
      <div className="users">

        {this.state.list.map(user =>
          <Card key={user.id} className="user">
            <CardBody>
              {user.merchant != null ? (
                <div>  
                  <div>name: {user.merchant.name}</div>
                  <div>totalTransactionSum: {user.merchant.totalTransactionSum}</div>
                  <div>status: {user.merchant.active ? 'Active' : 'Inactive User'}</div></div>
              ) : (
                ""
              )}
              <div>id: {user.id}</div>
              <div>email: {user.email}</div>
              <div>createdAt: {new Date(user.createdAt).toUTCString()}</div>
              <div>updatedAt: {new Date(user.updatedAt).toUTCString()}</div>

              <div><span>
                <Button onClick={() => this.edit(user)} variant="secondary" disabled={this.state.loading}>
                  Edit
                </Button>
                &nbsp;
                <Button onClick={() => this.delete(user.id)} variant="danger" disabled={this.state.loading}>
                  Delete
                </Button>
              </span></div>

            </CardBody></Card>
        )}
        {this.state.loading ? <div><ProgressBar animated={true} min={0} max={100} now={20} />&nbsp;</div> : ''}

        <p id="offsetline">
          {this.state.offset > 0 ? (
            <span>
              <Button onClick={() => this.firstPage()} disabled={this.state.loading}>
                First Page
              </Button>
              &nbsp;
              <Button onClick={() => this.previousPage()} disabled={this.state.loading}>
                Previous Page
              </Button>
            </span>
          ) : (
            ""
          )}
          &nbsp;
          {this.state.count > this.state.offset + TRANSACTIONS_PAGE_SIZE ? (
            <span>
              <Button onClick={() => this.nextPage()} disabled={this.state.loading}>
                Next Page
              </Button>
              &nbsp;
              <Button onClick={() => this.lastPage()} disabled={this.state.loading}>
                Last Page
              </Button>
            </span>
          ) : (
            ""
          )}
        </p>
        <p>{this.state.offset +  (this.state.list.length > 0 ? 1 : 0)} to {this.state.offset + this.state.list.length} Total Users {this.state.count}</p>

        <p>  <Button onClick={() => this.addUser(false)} disabled={this.state.loading}>
          Add Admin User
        </Button> <Button onClick={() => this.addUser(true)} disabled={this.state.loading}>
            Add Merchant User
          </Button>
        </p>
        <ImportUsersComponent callback={() => this.load()}></ImportUsersComponent>
        <AddEditUserComponent user={this.state.item} saveCallback={() => this.load()} cancelCallback={() => this.handleClose()}></AddEditUserComponent>
      </div>
    )
  }
}

export default UserListComponent