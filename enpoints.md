# Mini Project API Endpoints

## Feature 1: Event Discovery, Transaction, and Reviews

### 1. Event Discovery and Event Details

#### (a) GET /events

- **Description**: Retrieve a list of events.
- **Query Parameters**: `category`, `location`, `page`, `limit`, `search`
- **Response**: List of events with pagination and filtering.

    **Success Response:**
    ```json
    {
        "events": [
        {
            "id": 1,
            "name": "Music Concert",
            "description": "An amazing music concert.",
            "price": 500000,
            "date": "2024-07-01",
            "time": "19:00:00",
            "location": "Jakarta",
            "availableSeats": 150,
            "eventType": "Paid",
            "category": "Music",
            "eventPicture": "http://example.com/image.jpg",
            "organizer": {
                "id": 1,
                "name": "Organizer1",
                "profilePicture": "http://example.com/image.jpg"
            }
        }
        ],
        "pagination": {
        "page": 1,
        "limit": 10,
        "totalPages": 5
        }
    }
    ```
    **Failed Response:**
    ``` json
    {
        "error": "Invalid query parameters"
    }
    ```

#### (b) GET /events/{event_id}

- **Description**: Retrieve details of a specific event.
- **Path Parameters**: `event_id`
- **Response**: Event details including name, description, price, date, time, location, available seats, ticket types, and picture.

    **Success Response:**
    ```json
        {
            "id": 1,
            "name": "Music Concert",
            "description": "An amazing music concert.",
            "price": 500000,
            "date": "2024-07-01",
            "time": "19:00:00",
            "location": "Jakarta",
            "availableSeats": 150,
            "eventType": "Paid",
            "category": "Music",
            "eventPicture": "http://example.com/image.jpg"
        }
    ```

    **Failed Response:**
    ``` json
    {
        "error": "Event not found"
    }
    ```

#### (c) POST /tickets

- **Description**: Purchase tickets for an event.
- **Request Body:**:
    ```json
    {
        "eventId": 1,
        "userId": 2,
        "ticketPrice": 500000,
        "discountApplied": 50000,
        "finalPrice": 450000
    }
    ```
- **Response**: Ticket purchase confirmation.

    **Success Response:**
    ```json
        {
            "ticketId": 1,
            "eventId": 1,
            "userId": 2,
            "ticketPrice": 500000,
            "discountApplied": 50000,
            "finalPrice": 450000,
            "purchaseDate": "2024-06-19T14:48:00Z"
        }
    ```

    **Failed Response:**
    ``` json
    {
        "error": "Insufficient available seats"
    }
    ```

---

### 2. Event Transaction and Promotion

#### (a) POST /events

- **Description**: Create a new event.
- **Request Body**:
    ```json
    {
        "organizerId": 2,
        "name": "Music Concert",
        "description": "An amazing music concert.",
        "price": 500000,
        "date": "2024-07-01",
        "time": "19:00:00",
        "location": "Jakarta",
        "availableSeats": 150,
        "eventType": "Paid",
        "category": "Music",
        "eventPicture": "http://example.com/image.jpg"
    }
    ```
- **Response**: Created event details.

    **Success Response:**
    ```json
    {
        "id": 1,
        "organizerId": 2,
        "name": "Music Concert",
        "description": "An amazing music concert.",
        "price": 500000,
        "date": "2024-07-01",
        "time": "19:00:00",
        "location": "Jakarta",
        "availableSeats": 150,
        "eventType": "Paid",
        "category": "Music",
        "eventPicture": "http://example.com/image.jpg",
        "createdAt": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid input data"
    }
    ```

#### (b) POST /promotions

- **Description**: Create a promotion with discount vouchers.
- **Request Body:**:
    ```json
    {
        "userId": 2,
        "discountPercentage": 10.00,
        "expiryDate": "2024-12-31"
    }
    ```
- **Response**: Promotion creation confirmation.

    **Success Response:**
    ```json
    {
        "promotionId": 1,
        "userId": 2,
        "discountPercentage": 10.00,
        "expiryDate": "2024-12-31",
        "createdAt": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid input data"
    }
    ```
---

### 3. Event Reviews and Ratings

#### (a) POST /reviews

- **Description**: Leave a review and rating for an event.
- **Request Body:**:
    ```json
    {
        "eventId": 1,
        "userId": 2,
        "rating": 4,
        "reviewText": "Great event!"
    }
    ```
- **Response**: Review submission confirmation.

    **Success Response:**
    ```json
    {
        "reviewId": 1,
        "eventId": 1,
        "userId": 2,
        "rating": 4,
        "reviewText": "Great event!",
        "createdAt": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid input data"
    }
    ```
---
## Feature 2: User Authentication, Referral System, and Management Dashboard

### 1. User Authentication and Authorization

#### (a) POST /register

- **Description**: Register a new user.
- **Request Body:**:
    ```json
    {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "password": "securepassword",
        "referralCode": "REF12345"
    }
    ```
- **Response**: Registration confirmation and generated referral code.

    **Success Response:**
    ```json
    {
        "userId": 1,
        "referralCode": "REF67890",
        "createdAt": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Email already exists"
    }
    ```

#### (b) POST /login

- **Description**: Authenticate a user.
- **Request Body:**:
    ```json
    {
        "email": "john.doe@example.com",
        "password": "securepassword"
    }
    ```
- **Response**: Registration confirmation and generated referral code.

    **Success Response:**
    ```json
    {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid email or password"
    }
    ```

#### (c) GET /users/{user_id}

- **Description**: Retrieve user profile details.
- **Path Parameters:**: `user_id`
- **Response**: User profile information.
  
    **Success Response:**
    ```json
    {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "points": 20000,
        "profilePicture": "http://example.com/profile.jpg",
        "createdAt": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "User not found"
    }
    ```
---
### 2. Referral Number, Points, and Prizes

#### (a) POST /referrals

- **Description**: Use a referral code for registration.
- **Request Body:**:
    ```json
    {
        "referrerId": 1,
        "refereeId": 2
    }
    ```
- **Response**: Referral usage confirmation and points awarded.

    **Success Response:**
    ```json
    {
        "referralId": 1,
        "referrerId": 1,
        "refereeId": 2,
        "pointsEarned": 10000,
        "pointsExpiryDate": "2024-09-19",
        "createdAt": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Referral code invalid or already used"
    }
    ```

#### (b) GET /points/{user_id}

- **Description**: Retrieve user's points balance.
- **Path Parameters**: `user_id`
- **Response**: User points balance and expiry details.

    **Success Response:**
    ```json
    {
        "userId": 1,
        "pointsBalance": 20000,
        "points": [
            {
                "pointsEarned": 10000,
                "expiryDate": "2024-09-19"
            },
            {
                "pointsEarned": 10000,
                "expiryDate": "2024-12-19"
            }
        ]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "User not found"
    }
    ```

#### (c) POST /points/redemption

- **Description**: Redeem points for ticket discounts.
- **Request Body:**:
    ```json
    {
        "userId": 1,
        "ticketId": 2,
        "pointsRedeemed": 5000
    }
    ```
- **Response**: Points redemption confirmation.

    **Success Response:**
    ```json
    {
        "redemptionId": 1,
        "userId": 1,
        "ticketId": 2,
        "pointsRedeemed": 5000,
        "createdAt": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Insufficient points balance"
    }
    ```
---

### 3. Event Management Dashboard

#### (a) GET /dashboard/events

- **Description**: Retrieve events managed by an organizer.
- **Query Parameters**: `organizer_id`, `year`, `month`, `day`
- **Response**:  List of events with statistics.

    **Success Response:**
    ```json
    {
        "events": [
            {
            "id": 1,
            "name": "Music Concert",
            "date": "2024-07-01",
            "totalTicketsSold": 50,
            "totalRevenue": 25000000
            }
        ]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid query parameters"
    }
    ```

#### (b) GET /dashboard/statistics

- **Description**: Retrieve statistical data for an organizer's events.
- **Query Parameters**: `organizer_id`
- **Response**: Event statistics in graphical format.

    **Success Response:**
    ```json
    {
        "organizerId": 1,
        "statistics": {
            "totalEvents": 10,
            "totalTicketsSold": 500,
            "totalRevenue": 125000000
        }
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Organizer not found"
    }
    ```

#### (c) GET /dashboard/reports

- **Description**: Retrieve reports for an organizer's events.
- **Query Parameters**: `organizer_id`, `year`, `month`, `day`
- **Response**: Detailed reports of events.

    **Success Response:**
    ```json
    {
        "reports": [
            {
            "eventId": 1,
            "name": "Music Concert",
            "date": "2024-07-01",
            "ticketsSold": 50,
            "revenue": 25000000
            }
        ]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid query parameters"
    }
    ```
---

## Additional Endpoints

#### (a) GET /categories

- **Description**: Retrieve a list of event categories.
- **Response**: List of categories.

    **Success Response:**
    ```json
    {
        "categories": ["Music", "Sports", "Conference", "Workshop", "Theatre", "Exhibition", "Festival", "Others"]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Failed to retrieve categories"
    }
    ```

#### (b) GET /locations

- **Description**: Retrieve a list of event locations.
- **Response**: List of locations.

    **Success Response:**
    ```json
    {
        "locations": ["Jakarta", "Bandung", "Surabaya", "Yogyakarta", "Bali"]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Failed to retrieve locations"
    }
    ```




