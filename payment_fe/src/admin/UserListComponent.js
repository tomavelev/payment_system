import axios from "axios";
import './UserListComponent.css'
import { Component } from "react";
import { Button, Card, CardBody, Form, Modal } from "react-bootstrap";
import { TRANSACTIONS_PAGE_SIZE, host } from "..";

class UserListComponent extends Component {

  constructor(props) {
    super(props);
    this.state = {
      list: [],
      offset: 0,
      count: '',
      loading: false,
      item: null
    };
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
    })
    this.load()
  }
  previousPage() {
    this.setState({
      offset: this.state.offset - TRANSACTIONS_PAGE_SIZE
    })
    this.load()
  }
  nextPage() {
    this.setState({
      offset: this.state.offset + TRANSACTIONS_PAGE_SIZE
    })
    this.load()
  }
  lastPage() {
    this.setState({
      offset: this.state.count - TRANSACTIONS_PAGE_SIZE
    })
    this.load()

  }
  handleStatus(event) {
    var user = this.state.item;
    user.merchant.status = event.target.value;
    this.setState({
      item: user
    })
  }

  handleName(event) {
    var user = this.state.item;
    user.merchant.name = event.target.value;
    this.setState({
      item: user
    })
  }

  handleDescription(event) {
    var user = this.state.item;
    user.merchant.description = event.target.value;
    this.setState({
      item: user
    })
  }

  handleTransactionSum(event) {
    var user = this.state.item;
    user.merchant.totalTransactionSum = event.target.value;
    this.setState({
      item: user
    })
  }


  handleEmail(event) {
    var user = this.state.item;
    user.email = event.target.value;
    this.setState({
      item: user
    })
  }

  handlePassword(event) {
    var user = this.state.item;
    user.password = event.target.value;
    this.setState({
      item: user
    })
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
    this.setState({
      item: user
    })
    // this.load()
  }

  handleClose() {
    this.setState({
      item: null
    })
  }
  saveChanges() {
    axios.defaults.headers.common['Authorization'] = `Bearer ${localStorage.getItem("token")}` 

    axios.put(host + '/admin/user', this.state.item)
      .then((response) => {
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

  load() {
    axios.get(host + '/admin/users?offset=' + this.state.offset + "&limit=" + TRANSACTIONS_PAGE_SIZE,
      {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem("token")
        }
      })
      .then((response) => {
        if (this._isMounted)
          this.setState({
            list: response.data.list,
            count: response.data.count,
            item: null
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
      <div className="user">
        {
          this.state.item != null ? (
            <Modal show={this.state.item != null} onHide={() => this.handleClose()}>
              <Modal.Header closeButton>
                <Modal.Title>Edit User</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                <Form>

                  {this.state.item.merchant != null ? (
                    <div>

                      <Form.Check
                        value={this.state.item.merchant.status}
                        onChange={(e) => this.handleStatus(e)}
                        type="switch"
                        id="is-active"
                        label="Is Active"
                      />
                      <Form.Group className="mb-3" controlId="name"  >
                        <Form.Label>Name</Form.Label>
                        <Form.Control type="text" placeholder="Name" value={this.state.item.merchant.name} onChange={(e) => this.handleName(e)} />
                      </Form.Group>

                      <Form.Group className="mb-3" controlId="description">
                        <Form.Label>Description</Form.Label>
                        <Form.Control as="textarea" rows={3} type="text" placeholder="Description" value={this.state.item.merchant.description} onChange={(e) => this.handleDescription(e)} />
                      </Form.Group>

                      <Form.Group className="mb-3" controlId="totalTransactionSum"  >
                        <Form.Label>Total Transaction Sum</Form.Label>
                        <Form.Control type="number" placeholder="Total Transaction Sum" value={this.state.item.merchant.totalTransactionSum} onChange={(e) => this.handleTransactionSum(e)} />
                      </Form.Group>
                    </div>
                  ) : ("")}


                  <Form.Group className="mb-3" controlId="Email"  >
                    <Form.Label>Email</Form.Label>
                    <Form.Control type="email" placeholder="Email" value={this.state.item.email} onChange={(e) => this.handleEmail(e)} />
                  </Form.Group>
                  <Form.Group className="mb-3" controlId="Password"  >
                    <Form.Label>Password</Form.Label>
                    <Form.Control type="password" placeholder="Password" value={this.state.item.password} onChange={(e) => this.handlePassword(e)} />
                  </Form.Group>
                </Form>

              </Modal.Body>
              <Modal.Footer>
                <Button variant="secondary" onClick={() => this.handleClose()}>
                  Close
                </Button>
                <Button variant="primary" onClick={() => this.saveChanges()}>
                  Save Changes
                </Button>
              </Modal.Footer>
            </Modal>
          ) : ("")
        }
        {this.state.list.map(user =>
          <Card key={user.id} className="user">
            <CardBody>
              {user.merchant != null ? (
                <div>  <div>name: {user.merchant.name}</div>
                  <div>status: {user.merchant.status}</div></div>
              ) : (
                ""
              )}
              <div>id: {user.id}</div>
              <div>email: {user.email}</div>
              <div>createdAt: {new Date(user.createdAt).toUTCString()}</div>
              <div>updatedAt: {new Date(user.updatedAt).toUTCString()}</div>

              <div><span>
                <Button onClick={() => this.edit(user)} variant="secondary">
                  Edit
                </Button>
                &nbsp;
                <Button onClick={() => this.delete(user.id)} variant="danger">
                  Delete
                </Button>
              </span></div>

            </CardBody></Card>
        )}

        {this.state.offset > 0 ? (
          <span>
            <Button onClick={() => this.firstPage()}>
              First Page
            </Button>
            &nbsp;
            <Button onClick={() => this.previousPage()}>
              Previous Page
            </Button>
          </span>
        ) : (
          ""
        )}
        &nbsp;
        {this.state.count > this.state.offset + TRANSACTIONS_PAGE_SIZE ? (
          <span>
            <Button onClick={() => this.nextPage()}>
              Next Page
            </Button>
            &nbsp;
            <Button onClick={() => this.lastPage()}>
              Last Page
            </Button>
          </span>
        ) : (
          ""
        )}

        <div>{this.state.offset + 1} to {this.state.offset + this.state.list.length} Total Users {this.state.count}</div>
      </div>
    )
  }
}

export default UserListComponent