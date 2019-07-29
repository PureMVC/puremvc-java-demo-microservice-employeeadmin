employeeadmin = db.getSiblingDB("employeeadmin");

employeeadmin.department.drop();
employeeadmin.department.save([
    {
        name : "Accounting"
    },
    {
        name : "Sales"
    },
    {
        name : "Plant"
    },
    {
        name : "Shipping"
    },
    {
        name : "Quality Control"
    }
]);
// ----------------------------------------------
employeeadmin.role.drop();
employeeadmin.role.save([
    {
        name : "Administrator"
    },
    {
        name : "Accounts Payable"
    },
    {
        name : "Accounts Receivable"
    },
    {
        name : "Employee Benefits"
    },
    {
        name : "General Ledger"
    },
    {
        name : "Payroll"
    },
    {
        name : "Inventory"
    },
    {
        name : "Production"
    },
    {
        name : "Quality Control"
    },
    {
        name : "Sales"
    },
    {
        name : "Orders"
    },
    {
        name : "Customers"
    },
    {
        name : "Shipping"
    },
    {
        name : "Returns"
    }
]);
// ----------------------------------------------
employeeadmin.user.drop();
employeeadmin.user.save([
    {
        username: "lstooge",
        first: "Larry",
        last: "Stooge",
        email: "larry@stooges.com",
        department: {
            id: employeeadmin.department.findOne({name: "Accounting"})._id,
            name: "Accounting"
        },
        roles: [
            {
                id: employeeadmin.role.findOne({name: "Payroll"})._id,
                name: "Payroll"
            },
            {
                id: employeeadmin.role.findOne({name: "Employee Benefits"})._id,
                name: "Employee Benefits"
            }
        ]
    },
    {
        username: "cstooge",
        first: "Curly",
        last: "Stooge",
        email: "curly@stooges.com",
        department: {
            id: employeeadmin.department.findOne({name: "Sales"})._id,
            name: "Sales"
        },
        roles: [
            {
                id: employeeadmin.role.findOne({name: "Accounts Payable"})._id,
                name: "Accounts Payable"
            },
            {
                id: employeeadmin.role.findOne({name: "Accounts Receivable"})._id,
                name: "Accounts Receivable"
            },
            {
                id: employeeadmin.role.findOne({name: "General Ledger"})._id,
                name: "General Ledger"
            }
        ]
    },
    {
        username: "mstooge",
        first: "Moe",
        last: "Stooge",
        email: "moe@stooges.com",
        department: {
            id: employeeadmin.department.findOne({name: "Plant"})._id,
            name: "Plant"
        },
        roles: [
            {
                id: employeeadmin.role.findOne({name: "Inventory"})._id,
                name: "Inventory"
            },
            {
                id: employeeadmin.role.findOne({name: "Production"})._id,
                name: "Production"
            },
            {
                id: employeeadmin.role.findOne({name: "Sales"})._id,
                name: "Sales"
            },
            {
                id: employeeadmin.role.findOne({name: "Shipping"})._id,
                name: "Shipping"
            }
        ]
    }
]);