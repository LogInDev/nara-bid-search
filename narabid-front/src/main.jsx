import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import { BidInfoProvider } from './store/apiContext'

createRoot(document.getElementById('root')).render(
    <BidInfoProvider>
        <App />
    </BidInfoProvider>
)
