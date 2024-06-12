from flask import Flask, request, jsonify
from tensorflow.keras.preprocessing import image
from tensorflow.keras.models import load_model
from PIL import Image as PilImage
import numpy as np
import logging
import io

app = Flask(__name__)

# 모델과 클래스 레이블 로드
model = load_model('model.h5')
food_classes = ['apple_pie', 'baby_back_ribs', 'baklava', 'beef_carpaccio', 'beef_tartare', 'beet_salad', 'beignets', 'bibimbap', 'bread_pudding', 'breakfast_burrito', 'bruschetta', 'caesar_salad', 'cannoli', 'caprese_salad', 'carrot_cake', 'ceviche', 'cheesecake', 'cheese_plate', 'chicken_curry', 'chicken_quesadilla', 'chicken_wings', 'chocolate_cake', 'chocolate_mousse', 'churros', 'clam_chowder', 'club_sandwich', 'crab_cakes', 'creme_brulee', 'croque_madame', 'cup_cakes', 'deviled_eggs', 'donuts', 'dumplings', 'edamame', 'eggs_benedict', 'escargots', 'falafel', 'filet_mignon', 'fish_and_chips', 'foie_gras', 'french_fries', 'french_onion_soup', 'french_toast', 'fried_calamari', 'fried_rice', 'frozen_yogurt', 'garlic_bread', 'gnocchi', 'greek_salad', 'grilled_cheese_sandwich', 'grilled_salmon', 'guacamole', 'gyoza', 'hamburger', 'hot_and_sour_soup', 'hot_dog', 'huevos_rancheros', 'hummus', 'ice_cream', 'lasagna', 'lobster_bisque', 'lobster_roll_sandwich', 'macaroni_and_cheese', 'macarons', 'miso_soup', 'mussels', 'nachos', 'omelette', 'onion_rings', 'oysters', 'pad_thai', 'paella', 'pancakes', 'panna_cotta', 'peking_duck', 'pho', 'pizza', 'pork_chop', 'poutine', 'prime_rib', 'pulled_pork_sandwich', 'ramen', 'ravioli', 'red_velvet_cake', 'risotto', 'samosa', 'sashimi', 'scallops', 'seaweed_salad', 'shrimp_and_grits', 'spaghetti_bolognese', 'spaghetti_carbonara', 'spring_rolls', 'steak', 'strawberry_shortcake', 'sushi', 'tacos', 'takoyaki', 'tiramisu', 'tuna_tartare', 'waffles']

# 로깅 설정
logging.basicConfig(level=logging.INFO)

def prepare_image(image_stream):
    """이미지를 모델 입력 형식에 맞게 전처리하는 함수"""
    try:
        # image_stream을 PIL.Image.open을 사용하여 열기
        img = PilImage.open(image_stream)
        img = img.resize((299, 299))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array /= 255.0
        return img_array
    except Exception as e:
        logging.error(f"Image preparation error: {e}")
        raise

@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return "No image provided", 400
    
    img_file = request.files['image']
    
    try:
        img_array = prepare_image(img_file.stream)
        predictions = model.predict(img_array)
        predicted_class = food_classes[np.argmax(predictions)]
        return jsonify({"predicted_class": predicted_class})
    except Exception as e:
        logging.error(f"Prediction error: {e}")
        return str(e), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
