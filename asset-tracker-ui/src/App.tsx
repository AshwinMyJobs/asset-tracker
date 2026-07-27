import { useState, useEffect } from 'react';
import './App.css';
import keycloak from './keycloak'; // 1. Pull the active identity session reference

interface Asset {
  id: number;
  name: string;
  type: string;
  status: 'Active' | 'Maintenance' | 'Decommissioned';
  owner: string;
}

function App() {
  const [assets, setAssets] = useState<Asset[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const [formName, setFormName] = useState<string>('');
  const [formType, setFormType] = useState<string>('');
  const [formOwner, setFormOwner] = useState<string>('');
  const [formStatus, setFormStatus] = useState<'Active' | 'Maintenance' | 'Decommissioned'>('Active');

  // 2. SECURED READ OPERATION: Converted to async to await token validation
  useEffect(() => {
    const fetchSecureAssets = async () => {
      try {
        // Ensure token is fresh for the request
        await keycloak.updateToken(30);

        const res = await fetch('http://localhost:8080/api/assets', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${keycloak.token}`, // Inject JWT Access Token
            'Content-Type': 'application/json'
          }
        });

        if (!res.ok) throw new Error(`Server error: ${res.status}`);
        const data = await res.json();
        setAssets(data);
        setLoading(false);
      } catch (err: any) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchSecureAssets();
  }, []);

  // 3. SECURED MUTATION WRITE OPERATION
  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const newAssetPayload = {
      id: Date.now(),
      name: formName,
      type: formType,
      status: formStatus,
      owner: formOwner
    };

    try {
      // Ensure token is fresh before submitting form data
      await keycloak.updateToken(30);

      const res = await fetch('http://localhost:8080/api/assets', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${keycloak.token}`, // Inject JWT Access Token
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newAssetPayload)
      });

      if (!res.ok) throw new Error('Failed to save asset to database');
      const savedAsset: Asset = await res.json();

      setAssets([...assets, savedAsset]);
      setFormName('');
      setFormType('');
      setFormOwner('');
    } catch (err: any) {
      alert(`❌ Error saving asset: ${err.message}`);
    }
  };

  if (loading) return <div style={{ padding: '24px' }}>⏳ Loading asset registry...</div>;
  if (error) return <div style={{ padding: '24px', color: 'red' }}>❌ Error: {error}</div>;

  return (
    <div style={{ padding: '24px', fontFamily: 'Arial, sans-serif' }}>
      <header style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1>🏢 Enterprise Asset & Compliance Tracker</h1>
          <p>Senior IC Technical Playground Workspace</p>
        </div>
        {/* Added a handy secure logout button */}
        <button 
          onClick={() => keycloak.logout()} 
          style={{ padding: '8px 16px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
          🚪 Logout ({keycloak.tokenParsed?.preferred_username})
        </button>
      </header>

      <section style={{ marginBottom: '32px', padding: '16px', backgroundColor: '#f8f9fa', borderRadius: '8px', border: '1px solid #dee2e6' }}>
        <h3 style={{ marginTop: 0 }}>➕ Register New Enterprise Asset</h3>
        <form onSubmit={handleFormSubmit} style={{ display: 'grid', gap: '12px', maxWidth: '400px' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '4px' }}>Asset Name:</label>
            <input type="text" value={formName} onChange={(e) => setFormName(e.target.value)} required style={{ width: '100%', padding: '6px' }} />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '4px' }}>Asset Type:</label>
            <input type="text" value={formType} onChange={(e) => setFormType(e.target.value)} required style={{ width: '100%', padding: '6px' }} />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '4px' }}>Team Owner:</label>
            <input type="text" value={formOwner} onChange={(e) => setFormOwner(e.target.value)} required style={{ width: '100%', padding: '6px' }} />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '4px' }}>Operational Status:</label>
            <select value={formStatus} onChange={(e) => setFormStatus(e.target.value as any)} style={{ width: '100%', padding: '6px' }}>
              <option value="Active">Active</option>
              <option value="Maintenance">Maintenance</option>
              <option value="Decommissioned">Decommissioned</option>
            </select>
          </div>
          <button type="submit" style={{ padding: '10px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
            💾 Save to PostgreSQL via Spring Boot
          </button>
        </form>
      </section>

      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ backgroundColor: '#e9ecef', textAlign: 'left', borderBottom: '2px solid #dee2e6' }}>
            <th style={{ padding: '12px' }}>Asset ID</th>
            <th style={{ padding: '12px' }}>Name</th>
            <th style={{ padding: '12px' }}>Type</th>
            <th style={{ padding: '12px' }}>Status</th>
            <th style={{ padding: '12px' }}>Owner</th>
          </tr>
        </thead>
        <tbody>
          {assets.map((asset) => (
            <tr key={asset.id} style={{ borderBottom: '1px solid #dee2e6' }}>
              <td style={{ padding: '12px' }}>{asset.id}</td>
              <td style={{ padding: '12px', fontWeight: 'bold' }}>{asset.name}</td>
              <td style={{ padding: '12px' }}>{asset.type}</td>
              <td style={{ padding: '12px' }}>
                <span style={{
                  padding: '4px 8px',
                  borderRadius: '12px',
                  fontSize: '12px',
                  backgroundColor: asset.status === 'Active' ? '#d4edda' : asset.status === 'Maintenance' ? '#fff3cd' : '#f8d7da',
                  color: asset.status === 'Active' ? '#155724' : asset.status === 'Maintenance' ? '#856404' : '#721c24'
                }}>
                  {asset.status}
                </span>
              </td>
              <td style={{ padding: '12px' }}>{asset.owner}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;
