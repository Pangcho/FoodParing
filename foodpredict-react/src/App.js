import React, { useState } from 'react';
import axios from 'axios';

function App() {
  const [image, setImage] = useState(null);

  const handleImageUpload = (event) => {
    setImage(event.target.files[0]);
  };

  const handleSubmit = async () => {
    if (image) {
      const formData = new FormData();
      formData.append('file', image);

      try {
        const response = await axios.post('http://localhost:8090/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        console.log(response.data);
      } catch (error) {
        console.error('Error uploading image:', error);
      }
    }
  };

  return (
    <div>
      <input type="file" onChange={handleImageUpload} />
      <button onClick={handleSubmit}>Upload</button>
    </div>
  );
}

export default App;
